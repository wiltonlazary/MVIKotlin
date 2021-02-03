package com.arkivanov.mvikotlin.timetravel.client.internal.client

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore.Label
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore.State
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.TimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.badoo.reaktive.disposable.Disposable

internal interface TimeTravelClientStore : Store<Intent, State, Label> {

    sealed class Intent {
        object Connect : Intent()
        object Disconnect : Intent()
        object StartRecording : Intent()
        object StopRecording : Intent()
        object MoveToStart : Intent()
        object StepBackward : Intent()
        object StepForward : Intent()
        object MoveToEnd : Intent()
        object Cancel : Intent()
        object DebugEvent : Intent()
        data class SelectEvent(val index: Int) : Intent()
        object ExportEvents : Intent()
        class ImportEvents(val data: ByteArray) : Intent()
    }

    sealed class State {
        object Disconnected : State()

        data class Connecting(
            /*private*/ internal val disposable: Disposable
        ) : State()

        data class Connected(
            val events: List<TimeTravelEvent> = emptyList(),
            val currentEventIndex: Int = -1,
            val mode: TimeTravelStateUpdate.Mode = TimeTravelStateUpdate.Mode.IDLE,
            val selectedEventIndex: Int = -1,
            /*private*/ internal val disposable: Disposable,
            /*private*/ internal val writer: (TimeTravelCommand) -> Unit
        ) : State()
    }

    sealed class Label {
        class ExportEvents(val data: ByteArray) : Label()
        data class Error(val text: String?) : Label()
    }
}
