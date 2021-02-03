package com.arkivanov.mvikotlin.timetravel.client.internal.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStoreFactory
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

internal class TimeTravelSettingsStoreSettingsPersistent(
    settingsFactory: Settings.Factory
) : TimeTravelSettingsStoreFactory.Settings {

    private val storage = settingsFactory.create(name = SETTINGS_NAME)

    override var settings: State.Settings
        get() = State.Settings(
            host = storage[KEY_HOST, DEFAULT_HOST],
            port = storage[KEY_PORT, DEFAULT_PORT],
            wrapEventDetails = storage[KEY_WRAP_EVENT_DETAILS, false]
        )
        set(value) {
            storage[KEY_HOST] = value.host
            storage[KEY_PORT] = value.port
            storage[KEY_WRAP_EVENT_DETAILS] = value.wrapEventDetails
        }

    private companion object {
        private const val SETTINGS_NAME = "TimeTravelSettings"
        private const val KEY_HOST = "HOST"
        private const val KEY_PORT = "PORT"
        private const val KEY_WRAP_EVENT_DETAILS = "WRAP_EVENT_DETAILS"
        private const val DEFAULT_HOST = "localhost"
    }
}
