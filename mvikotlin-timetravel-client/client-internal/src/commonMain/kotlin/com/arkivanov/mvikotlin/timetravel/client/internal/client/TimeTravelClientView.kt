package com.arkivanov.mvikotlin.timetravel.client.internal.client

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Model
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.Value

interface TimeTravelClientView : MviView<Model, Event> {

    fun execute(action: Action)

    data class Model(
        val events: List<String>,
        val currentEventIndex: Int,
        val buttons: Buttons,
        val selectedEventIndex: Int,
        val selectedEventValue: Value?,
        val wrapEventDetails: Boolean
    ) {
        data class Buttons(
            val isConnectEnabled: Boolean,
            val isDisconnectEnabled: Boolean,
            val isStartRecordingEnabled: Boolean,
            val isStopRecordingEnabled: Boolean,
            val isMoveToStartEnabled: Boolean,
            val isStepBackwardEnabled: Boolean,
            val isStepForwardEnabled: Boolean,
            val isMoveToEndEnabled: Boolean,
            val isCancelEnabled: Boolean,
            val isDebugEventEnabled: Boolean,
            val isExportEventsEnabled: Boolean,
            val isImportEventsEnabled: Boolean
        )
    }

    sealed class Event {
        object ConnectClicked : Event()
        object DisconnectClicked : Event()
        object StartRecordingClicked : Event()
        object StopRecordingClicked : Event()
        object MoveToStartClicked : Event()
        object StepBackwardClicked : Event()
        object StepForwardClicked : Event()
        object MoveToEndClicked : Event()
        object CancelClicked : Event()
        object DebugEventClicked : Event()
        object ShowSettingsClicked : Event()
        data class EventSelected(val index: Int) : Event()
        object ExportEventsClicked : Event()
        object ImportEventsClicked : Event()
        class ImportEventsConfirmed(val data: ByteArray) : Event()
    }

    sealed class Action {
        class ExportEvents(val data: ByteArray) : Action()
        object ImportEvents : Action()
        data class ShowError(val text: String) : Action()
    }
}
