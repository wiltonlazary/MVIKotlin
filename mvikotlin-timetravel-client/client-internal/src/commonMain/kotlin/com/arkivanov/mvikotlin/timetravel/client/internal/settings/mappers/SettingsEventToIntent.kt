package com.arkivanov.mvikotlin.timetravel.client.internal.settings.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Event

internal val settingsEventToIntent: Event.() -> Intent? =
    {
        when (this) {
            is Event.SaveClicked -> Intent.SaveEdit
            is Event.CancelClicked -> Intent.CancelEdit
            is Event.HostChanged -> Intent.EditHost(host = host)
            is Event.PortChanged -> Intent.EditPort(port = port)
            is Event.WrapEventDetailsChanged -> Intent.SetWrapEventDetails(wrapEventDetails = wrapEventDetails)
        }
    }
