package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClient
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.scheduler.overrideSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    overrideSchedulers(main = Dispatchers.Main::asScheduler)
    invokeOnAwtSync { setMainThreadId(Thread.currentThread().id) }

    val clientView = invokeOnAwtSync { TimeTravelClientViewProxy() }
    val settingsView = invokeOnAwtSync { TimeTravelSettingsViewProxy() }

    invokeOnAwtSync {
        TimeTravelClient(
            enableSettings = true,
            clientView = clientView,
            settingsView = settingsView
        ).apply { onCreate() }
    }

    Window(
        title = "MVIKotlin Time Travel Client",
        size = getPreferredWindowSize(desiredWidth = 1920, desiredHeight = 1080)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            MaterialTheme {
                DesktopTheme {
                    Box {
//                    WithCustomPopups {
                        Main(view = clientView)
                        Settings(view = settingsView)
//                    }
                    }
                }
            }
        }
    }
}
