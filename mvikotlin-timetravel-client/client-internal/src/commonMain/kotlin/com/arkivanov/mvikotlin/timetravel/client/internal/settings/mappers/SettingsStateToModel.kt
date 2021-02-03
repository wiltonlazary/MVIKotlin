package com.arkivanov.mvikotlin.timetravel.client.internal.settings.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Model

internal val settingsStateToModel: State.() -> Model =
    {
        Model(editing = editing?.toEditingModel())
    }

private fun State.Editing.toEditingModel(): Model.Editing =
    Model.Editing(
        host = host,
        port = port,
        wrapEventDetails = wrapEventDetails
    )
