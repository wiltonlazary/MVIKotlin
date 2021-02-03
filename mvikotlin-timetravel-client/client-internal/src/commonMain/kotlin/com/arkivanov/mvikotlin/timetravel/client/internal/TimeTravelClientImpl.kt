package com.arkivanov.mvikotlin.timetravel.client.internal

import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView
import com.arkivanov.mvikotlin.timetravel.client.internal.client.mappers.clientEventToAction
import com.arkivanov.mvikotlin.timetravel.client.internal.client.mappers.clientEventToIntent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.mappers.clientLabelToAction
import com.arkivanov.mvikotlin.timetravel.client.internal.client.mappers.clientStateToModel
import com.arkivanov.mvikotlin.timetravel.client.internal.integration.TimeTravelSettingsStoreSettingsMemory
import com.arkivanov.mvikotlin.timetravel.client.internal.integration.TimeTravelSettingsStoreSettingsPersistent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.mappers.clientEventToSettingsIntent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.mappers.settingsEventToIntent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.mappers.settingsStateToModel
import com.badoo.reaktive.observable.combineLatest
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull
import com.russhwolf.settings.Settings

internal class TimeTravelClientImpl(
    connector: TimeTravelClientStoreFactory.Connector,
    settingsFactory: Settings.Factory?,
    clientView: TimeTravelClientView,
    settingsView: TimeTravelSettingsView
) : TimeTravelClient {

    private val settingsStore =
        TimeTravelSettingsStoreFactory(
            storeFactory = DefaultStoreFactory,
            settings = settingsFactory?.let(::TimeTravelSettingsStoreSettingsPersistent) ?: TimeTravelSettingsStoreSettingsMemory()
        ).create()

    private val clientStore =
        TimeTravelClientStoreFactory(
            storeFactory = DefaultStoreFactory,
            connector = connector,
            host = { settingsStore.state.settings.host },
            port = { settingsStore.state.settings.port }
        ).create()

    private val binder =
        bind {
            combineLatest(
                clientStore.states,
                settingsStore.states,
                clientStateToModel
            ) bindTo clientView

            clientStore.labels.mapNotNull(clientLabelToAction) bindTo clientView::execute
            clientView.events.mapNotNull(clientEventToIntent) bindTo clientStore
            clientView.events.mapNotNull(clientEventToAction) bindTo clientView::execute
            clientView.events.mapNotNull(clientEventToSettingsIntent) bindTo settingsStore

            settingsStore.states.map(settingsStateToModel) bindTo settingsView
            settingsView.events.mapNotNull(settingsEventToIntent) bindTo settingsStore
        }

    override fun onCreate() {
        binder.start()
    }

    override fun onDestroy() {
        binder.stop()
        clientStore.dispose()
        settingsStore.dispose()
    }
}
