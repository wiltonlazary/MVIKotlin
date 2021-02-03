package com.arkivanov.mvikotlin.timetravel.client.internal.settings.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.Intent

internal val clientEventToSettingsIntent: Event.() -> Intent? =
    {
        when (this) {
            is Event.ShowSettingsClicked -> Intent.StartEdit

            is Event.ConnectClicked,
            is Event.DisconnectClicked,
            is Event.StartRecordingClicked,
            is Event.StopRecordingClicked,
            is Event.MoveToStartClicked,
            is Event.StepBackwardClicked,
            is Event.StepForwardClicked,
            is Event.MoveToEndClicked,
            is Event.CancelClicked,
            is Event.DebugEventClicked,
            is Event.EventSelected,
            is Event.ExportEventsClicked,
            is Event.ImportEventsClicked,
            is Event.ImportEventsConfirmed -> null
        }
    }
