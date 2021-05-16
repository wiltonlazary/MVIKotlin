package com.arkivanov.mvikotlin.plugin.idea.timetravel

import androidx.compose.desktop.ComposePanel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller.DefaultAdbController
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelClientComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelConnectorFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.ui.TimeTravelClientUi
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.DefaultSettings
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.russhwolf.settings.JvmPreferencesSettings
import kotlinx.coroutines.Dispatchers

class TimeTravelToolWindowFactory : ToolWindowFactory {

    init {
        overrideSchedulers(main = Dispatchers.Main::asScheduler)
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setIcon(AllIcons.Debugger.Db_muted_dep_line_breakpoint)

        toolWindow.contentManager.addContent(
            ContentFactory
                .SERVICE
                .getInstance()
                .createContent(timeTravelPanel(), "", false)
        )
    }

    private fun timeTravelPanel(): ComposePanel {
        val client = client()

        return ComposePanel().apply {
            setContent {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TimeTravelClientUi(client)
                }
            }
        }
    }

    private fun client(): TimeTravelClient =
        TimeTravelClientComponent(
            lifecycle = TimeTravelToolWindowListener.getLifecycle(),
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
        )

    private fun importEvents(): ByteArray? {
        return null
    }

    private fun exportEvents(data: ByteArray) {
    }

    private fun selectAdbPath(): String? {
        return null
    }

    companion object {
        const val TOOL_WINDOW_ID = "MVIKotlin Time Travel"
    }
}
