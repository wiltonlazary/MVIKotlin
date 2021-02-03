package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Model

internal class TimeTravelSettingsViewImpl : BaseMviView<Model, Event>(), TimeTravelSettingsView
