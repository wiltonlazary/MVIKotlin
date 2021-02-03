package com.arkivanov.mvikotlin.timetravel.client.internal.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStoreFactory
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT

internal class TimeTravelSettingsStoreSettingsMemory : TimeTravelSettingsStoreFactory.Settings {

    override var settings: State.Settings =
        State.Settings(
            host = DEFAULT_HOST,
            port = DEFAULT_PORT,
            wrapEventDetails = false
        )

    private companion object {
        private const val DEFAULT_HOST = "localhost"
    }
}
