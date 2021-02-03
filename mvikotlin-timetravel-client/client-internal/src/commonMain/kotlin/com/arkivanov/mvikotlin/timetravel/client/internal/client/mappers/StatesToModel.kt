package com.arkivanov.mvikotlin.timetravel.client.internal.client.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Model
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.storeeventtype.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.type

internal val clientStateToModel: (TimeTravelClientStore.State, TimeTravelSettingsStore.State) -> Model =
    { clientState, settingsState ->
        when (clientState) {
            is TimeTravelClientStore.State.Disconnected,
            is TimeTravelClientStore.State.Connecting ->
                Model(
                    events = emptyList(),
                    currentEventIndex = -1,
                    buttons = clientState.toButtons(),
                    selectedEventIndex = -1,
                    selectedEventValue = null,
                    wrapEventDetails = settingsState.settings.wrapEventDetails
                )

            is TimeTravelClientStore.State.Connected ->
                Model(
                    events = clientState.events.map(TimeTravelEvent::text),
                    currentEventIndex = clientState.currentEventIndex,
                    buttons = clientState.toButtons(),
                    selectedEventIndex = clientState.selectedEventIndex,
                    selectedEventValue = clientState.events.getOrNull(clientState.selectedEventIndex)?.value,
                    wrapEventDetails = settingsState.settings.wrapEventDetails
                )
        }
    }

private val TimeTravelEvent.text: String get() = "[$storeName]: ${type.title}.${value.type}"

private fun TimeTravelClientStore.State.toButtons(): Model.Buttons =
    Model.Buttons(
        isConnectEnabled = isDisconnected(),
        isDisconnectEnabled = !isDisconnected(),
        isStartRecordingEnabled = isModeIdle(),
        isStopRecordingEnabled = isModeRecording(),
        isMoveToStartEnabled = isModeStopped(),
        isStepBackwardEnabled = isModeStopped(),
        isStepForwardEnabled = isModeStopped(),
        isMoveToEndEnabled = isModeStopped(),
        isCancelEnabled = isModeRecording() || isModeStopped(),
        isDebugEventEnabled = isDebuggableEventSelected(),
        isExportEventsEnabled = isModeStopped(),
        isImportEventsEnabled = isModeIdle()
    )

private fun TimeTravelClientStore.State.isModeIdle(): Boolean =
    when (this) {
        is TimeTravelClientStore.State.Disconnected,
        is TimeTravelClientStore.State.Connecting -> false
        is TimeTravelClientStore.State.Connected -> mode == TimeTravelStateUpdate.Mode.IDLE
    }

private fun TimeTravelClientStore.State.isModeRecording(): Boolean =
    when (this) {
        is TimeTravelClientStore.State.Disconnected,
        is TimeTravelClientStore.State.Connecting -> false
        is TimeTravelClientStore.State.Connected -> mode == TimeTravelStateUpdate.Mode.RECORDING
    }

private fun TimeTravelClientStore.State.isModeStopped(): Boolean =
    when (this) {
        is TimeTravelClientStore.State.Disconnected,
        is TimeTravelClientStore.State.Connecting -> false
        is TimeTravelClientStore.State.Connected -> mode == TimeTravelStateUpdate.Mode.STOPPED
    }

private fun TimeTravelClientStore.State.isDisconnected(): Boolean =
    when (this) {
        is TimeTravelClientStore.State.Disconnected -> true
        is TimeTravelClientStore.State.Connecting,
        is TimeTravelClientStore.State.Connected -> false
    }

private fun TimeTravelClientStore.State.isDebuggableEventSelected(): Boolean =
    when (this) {
        is TimeTravelClientStore.State.Disconnected,
        is TimeTravelClientStore.State.Connecting -> false
        is TimeTravelClientStore.State.Connected -> events.getOrNull(selectedEventIndex)?.type?.isDebuggable() == true
    }

private fun StoreEventType.isDebuggable(): Boolean =
    when (this) {
        StoreEventType.INTENT,
        StoreEventType.ACTION,
        StoreEventType.RESULT,
        StoreEventType.LABEL -> true
        StoreEventType.STATE -> false
    }
