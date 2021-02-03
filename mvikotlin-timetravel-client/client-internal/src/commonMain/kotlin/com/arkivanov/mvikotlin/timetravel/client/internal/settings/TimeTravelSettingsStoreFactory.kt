package com.arkivanov.mvikotlin.timetravel.client.internal.settings

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.State

internal class TimeTravelSettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val settings: Settings
) {

    fun create(): TimeTravelSettingsStore =
        object : TimeTravelSettingsStore, Store<Intent, State, Nothing> by storeFactory.create(
            initialState = State(settings = settings.settings),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Result {
        object EditRequested : Result()
        data class EditFinished(val newSettings: State.Settings) : Result()
        object EditCancelled : Result()
        data class HostChanged(val host: String) : Result()
        data class PortChanged(val port: String) : Result()
        data class WrapEventDetailsChanged(val wrapEventDetails: Boolean) : Result()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.StartEdit -> dispatch(Result.EditRequested)
                is Intent.SaveEdit -> saveEdit(state = getState())
                is Intent.CancelEdit -> dispatch(Result.EditCancelled)
                is Intent.EditHost -> dispatch(Result.HostChanged(host = intent.host))
                is Intent.EditPort -> dispatch(Result.PortChanged(port = intent.port))
                is Intent.SetWrapEventDetails -> dispatch(Result.WrapEventDetailsChanged(wrapEventDetails = intent.wrapEventDetails))
            }

        private fun saveEdit(state: State) {
            val newSettings = state.editing?.toSettings() ?: return
            settings.settings = newSettings
            dispatch(Result.EditFinished(newSettings = newSettings))
        }

        private fun State.Editing.toSettings(): State.Settings? {
            return State.Settings(
                host = host,
                port = port.toIntOrNull() ?: return null,
                wrapEventDetails = wrapEventDetails
            )
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.EditRequested -> copy(editing = settings.toEditing())
                is Result.EditFinished -> copy(settings = result.newSettings, editing = null)
                is Result.EditCancelled -> copy(editing = null)
                is Result.HostChanged -> copy(editing = editing?.copy(host = result.host))
                is Result.PortChanged -> copy(editing = editing?.copy(port = result.port))
                is Result.WrapEventDetailsChanged -> copy(editing = editing?.copy(wrapEventDetails = result.wrapEventDetails))
            }

        private fun State.Settings.toEditing(): State.Editing =
            State.Editing(
                host = host,
                port = port.toString(),
                wrapEventDetails = wrapEventDetails
            )
    }

    interface Settings {
        var settings: State.Settings
    }
}
