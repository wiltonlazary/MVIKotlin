package com.arkivanov.mvikotlin.timetravel.client.internal.settings

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.State

internal interface TimeTravelSettingsStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        object StartEdit : Intent()
        object SaveEdit : Intent()
        object CancelEdit : Intent()
        data class EditHost(val host: String) : Intent()
        data class EditPort(val port: String) : Intent()
        data class SetWrapEventDetails(val wrapEventDetails: Boolean) : Intent()
    }

    data class State(
        val settings: Settings,
        val editing: Editing? = null
    ) {
        data class Settings(
            val host: String,
            val port: Int,
            val wrapEventDetails: Boolean
        )

        data class Editing(
            val host: String,
            val port: String,
            val wrapEventDetails: Boolean
        )
    }
}
