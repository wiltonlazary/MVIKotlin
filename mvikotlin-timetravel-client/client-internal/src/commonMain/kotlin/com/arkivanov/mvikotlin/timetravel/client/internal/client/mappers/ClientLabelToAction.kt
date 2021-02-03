package com.arkivanov.mvikotlin.timetravel.client.internal.client.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStore
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Action

internal val clientLabelToAction: TimeTravelClientStore.Label.() -> Action? =
    {
        when (this) {
            is TimeTravelClientStore.Label.ExportEvents -> Action.ExportEvents(data = data)
            is TimeTravelClientStore.Label.Error -> Action.ShowError(text = text ?: "Unknown error")
        }
    }
