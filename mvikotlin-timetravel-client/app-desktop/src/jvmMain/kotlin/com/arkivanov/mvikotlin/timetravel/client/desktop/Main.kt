package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.resume
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller.DefaultAdbController
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelClientComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelConnectorFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.ui.TimeTravelClientUi
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.theme.TimeTravelClientTheme
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.DefaultSettings
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.russhwolf.settings.JvmPreferencesSettings
import kotlinx.coroutines.Dispatchers
import java.awt.FileDialog
import java.awt.Frame
import java.io.FilenameFilter

fun main() {
    overrideSchedulers(main = Dispatchers.Main::asScheduler)

    val client =
        invokeOnAwtSync {
            setMainThreadId(Thread.currentThread().id)
            client()
        }

    Window(
        title = "MVIKotlin Time Travel Client",
        size = getPreferredWindowSize(desiredWidth = 1920, desiredHeight = 1080)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            TimeTravelClientTheme {
                DesktopTheme {
                    TimeTravelClientUi(client)
                }
            }
        }
    }
}

private fun client(): TimeTravelClient {
    val lifecycle = LifecycleRegistry()

    return TimeTravelClientComponent(
        lifecycle = lifecycle,
        storeFactory = DefaultStoreFactory,
        settingsFactory = JvmPreferencesSettings.Factory(),
        connectorFactory = TimeTravelConnectorFactory(),
        defaultSettings = DefaultSettings(),
        adbController = DefaultAdbController(
            settingsFactory = JvmPreferencesSettings.Factory(),
            selectAdbPath = ::selectAdbPath
        ),
        onImportEvents = ::importEvents,
        onExportEvents = ::exportEvents
    ).also {
        lifecycle.resume()
    }
}

private fun importEvents(): ByteArray? {
    val dialog = FileDialog(null as Frame?, "MVIKotlin time travel import", FileDialog.LOAD)
    dialog.filenameFilter = FilenameFilter { _, name -> name.endsWith(".tte") }
    dialog.isVisible = true

    return dialog
        .selectedFile
        ?.readBytes()
}

private fun exportEvents(data: ByteArray) {
    val dialog = FileDialog(null as Frame?, "MVIKotlin time travel export", FileDialog.SAVE)
    dialog.file = "TimeTravelEvents.tte"
    dialog.isVisible = true

    dialog
        .selectedFile
        ?.writeBytes(data)
}

private fun selectAdbPath(): String? {
    val dialog = FileDialog(null as Frame?, "Select ADB executable path", FileDialog.LOAD)
    dialog.filenameFilter = FilenameFilter { _, name -> name == "adb" }
    dialog.isVisible = true

    return dialog
        .selectedFile
        ?.absolutePath
}
