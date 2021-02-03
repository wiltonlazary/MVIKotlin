package com.arkivanov.mvikotlin.timetravel.client.internal.settings

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Model

interface TimeTravelSettingsView : MviView<Model, Event> {

    data class Model(
        val editing: Editing?
    ) {
        data class Editing(
            val host: String,
            val port: String,
            val wrapEventDetails: Boolean
        )
    }

    sealed class Event {
        object SaveClicked : Event()
        object CancelClicked : Event()
        data class HostChanged(val host: String) : Event()
        data class PortChanged(val port: String) : Event()
        data class WrapEventDetailsChanged(val wrapEventDetails: Boolean) : Event()
    }
}
