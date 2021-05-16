package com.arkivanov.mvikotlin.timetravel.client.internal.client

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore.Label
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore.State
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.doOnBeforeSubscribe

internal class TimeTravelClientStoreFactory(
    private val storeFactory: StoreFactory,
    private val connector: Connector,
    private val host: () -> String,
    private val port: () -> Int
) {

    fun create(): TimeTravelClientStore =
        object : TimeTravelClientStore, Store<Intent, State, Label> by storeFactory.create(
            initialState = State.Disconnected,
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Result {
        class Connecting(val disposable: Disposable) : Result()
        class Connected(val writer: (TimeTravelCommand) -> Unit) : Result()
        object Disconnected : Result()
        class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Result()
        class EventSelected(val index: Int) : Result()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.Connect -> connectIfNeeded(getState())
                is Intent.Disconnect -> disconnectIfNeeded(getState())
                is Intent.StartRecording -> sendIfNeeded(getState()) { TimeTravelCommand.StartRecording }
                is Intent.StopRecording -> sendIfNeeded(getState()) { TimeTravelCommand.StopRecording }
                is Intent.MoveToStart -> sendIfNeeded(getState()) { TimeTravelCommand.MoveToStart }
                is Intent.StepBackward -> sendIfNeeded(getState()) { TimeTravelCommand.StepBackward }
                is Intent.StepForward -> sendIfNeeded(getState()) { TimeTravelCommand.StepForward }
                is Intent.MoveToEnd -> sendIfNeeded(getState()) { TimeTravelCommand.MoveToEnd }
                is Intent.Cancel -> sendIfNeeded(getState()) { TimeTravelCommand.Cancel }
                is Intent.DebugEvent -> debugEventIfNeeded(getState())
                is Intent.SelectEvent -> selectEvent(intent.index)
                is Intent.ExportEvents -> sendIfNeeded(getState()) { TimeTravelCommand.ExportEvents }
                is Intent.ImportEvents -> sendIfNeeded(getState()) { TimeTravelCommand.ImportEvents(intent.data) }
            }

        private fun connectIfNeeded(state: State): Unit =
            when (state) {
                is State.Disconnected -> connect()
                is State.Connecting,
                is State.Connected -> Unit
            }

        private fun connect() {
            connector
                .connect(host = host(), port = port())
                .doOnBeforeSubscribe { dispatch(Result.Connecting(it)) }
                .doOnBeforeFinally { dispatch(Result.Disconnected) }
                .subscribeScoped(onNext = ::onEvent)
        }

        private fun onEvent(event: Connector.Event): Unit =
            when (event) {
                is Connector.Event.Connected -> dispatch(Result.Connected(event.writer))
                is Connector.Event.StateUpdate -> dispatch(Result.StateUpdate(event.stateUpdate))
                is Connector.Event.ExportEvents -> publish(Label.ExportEvents(event.data))
                is Connector.Event.Error -> publish(Label.Error(event.text))
            }

        private fun disconnectIfNeeded(state: State) {
            val disposable =
                when (state) {
                    is State.Disconnected -> return
                    is State.Connecting -> state.disposable
                    is State.Connected -> state.disposable
                }

            disposable.dispose()
            dispatch(Result.Disconnected)
        }

        private inline fun sendIfNeeded(state: State, command: State.Connected.() -> TimeTravelCommand?): Unit =
            when (state) {
                is State.Disconnected,
                is State.Connecting -> Unit
                is State.Connected -> {
                    state.command()?.also(state.writer)
                    Unit
                }
            }

        private fun debugEventIfNeeded(state: State) {
            sendIfNeeded(state) {
                when (state) {
                    is State.Disconnected,
                    is State.Connecting -> null
                    is State.Connected -> state.events.getOrNull(state.selectedEventIndex)?.id?.let(TimeTravelCommand::DebugEvent)
                }
            }
        }

        private fun selectEvent(index: Int) {
            dispatch(Result.EventSelected(index = index))
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Connecting -> State.Connecting(disposable = result.disposable)
                is Result.Connected -> applyConnected(result)
                is Result.Disconnected -> State.Disconnected
                is Result.StateUpdate -> applyStateUpdate(result)
                is Result.EventSelected -> applyEventSelected(result)
            }

        private fun State.applyConnected(result: Result.Connected): State =
            when (this) {
                is State.Disconnected,
                is State.Connected -> this
                is State.Connecting -> State.Connected(disposable = disposable, writer = result.writer)
            }

        private fun State.applyStateUpdate(result: Result.StateUpdate): State =
            when (this) {
                is State.Disconnected,
                is State.Connecting -> this
                is State.Connected -> applyUpdate(result.stateUpdate)
            }

        private fun State.Connected.applyUpdate(update: TimeTravelStateUpdate): State.Connected =
            copy(
                events = events.applyUpdate(update = update.eventsUpdate),
                currentEventIndex = update.selectedEventIndex,
                mode = update.mode,
                selectedEventIndex = selectedEventIndex.coerceAtMost(events.lastIndex)
            )

        private fun List<TimeTravelEvent>.applyUpdate(update: TimeTravelEventsUpdate): List<TimeTravelEvent> =
            when (update) {
                is TimeTravelEventsUpdate.All -> update.events
                is TimeTravelEventsUpdate.New -> this + update.events
            }

        private fun State.applyEventSelected(result: Result.EventSelected): State =
            when (this) {
                is State.Disconnected,
                is State.Connecting -> this
                is State.Connected -> copy(selectedEventIndex = result.index)
            }

        private fun State.applyEventUnselected(): State =
            when (this) {
                is State.Disconnected,
                is State.Connecting -> this
                is State.Connected -> copy(selectedEventIndex = -1)
            }
    }

    interface Connector {
        @EventsOnMainScheduler
        fun connect(host: String, port: Int): Observable<Event>

        sealed class Event {
            class Connected(val writer: (TimeTravelCommand) -> Unit) : Event()
            class StateUpdate(val stateUpdate: TimeTravelStateUpdate) : Event()
            class ExportEvents(val data: ByteArray) : Event()
            class Error(val text: String?) : Event()
        }
    }
}
