package com.arkivanov.mvikotlin.timetravel.client.desktop

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Model

class TimeTravelSettingsViewProxy : MviViewProxy<Model, Event>(), TimeTravelSettingsView
