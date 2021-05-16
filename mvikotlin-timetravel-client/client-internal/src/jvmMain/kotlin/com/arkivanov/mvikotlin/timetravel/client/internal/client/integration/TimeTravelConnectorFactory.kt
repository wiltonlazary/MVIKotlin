package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStoreFactory

actual class TimeTravelConnectorFactory {

    internal actual fun create(): TimeTravelClientStoreFactory.Connector = TimeTravelClientStoreConnector()
}
