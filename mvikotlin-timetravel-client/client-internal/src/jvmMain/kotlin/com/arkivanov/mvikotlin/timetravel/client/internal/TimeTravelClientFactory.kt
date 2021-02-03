package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPreferencesSettings

@OptIn(ExperimentalSettingsImplementation::class)
@Suppress("FunctionName") // Factory function
fun TimeTravelClient(
    enableSettings: Boolean,
    clientView: TimeTravelClientView,
    settingsView: TimeTravelSettingsView
): TimeTravelClient =
    TimeTravelClientImpl(
        connector = TimeTravelConnector(),
        settingsFactory = if (enableSettings) JvmPreferencesSettings.Factory() else null,
        clientView = clientView,
        settingsView = settingsView
    )
