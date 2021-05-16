package com.arkivanov.mvikotlin.timetravel.client.internal.settings

import androidx.compose.runtime.State

interface TimeTravelSettings {

    val models: State<Model>

    fun onEditClicked()
    fun onSaveClicked()
    fun onCancelClicked()
    fun onHostChanged(host: String)
    fun onPortChanged(port: String)
    fun onConnectViaAdbChanged(connectViaAdb: Boolean)
    fun onWrapEventDetailsChanged(wrapEventDetails: Boolean)

    data class Model(
        val settings: Settings,
        val editing: Editing?
    ) {
        data class Settings(
            val host: String,
            val port: Int,
            val connectViaAdb: Boolean,
            val wrapEventDetails: Boolean
        )

        data class Editing(
            val host: String,
            val port: String,
            val connectViaAdb: Boolean,
            val wrapEventDetails: Boolean
        )
    }
}
