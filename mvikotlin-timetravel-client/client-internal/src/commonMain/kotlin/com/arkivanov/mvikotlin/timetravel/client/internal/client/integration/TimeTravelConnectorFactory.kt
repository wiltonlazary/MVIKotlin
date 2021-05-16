package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStoreFactory

expect class TimeTravelConnectorFactory {

    internal fun create(): TimeTravelClientStoreFactory.Connector
}
