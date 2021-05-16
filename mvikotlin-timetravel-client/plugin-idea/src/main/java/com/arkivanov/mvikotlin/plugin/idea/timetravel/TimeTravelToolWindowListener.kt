package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.destroy
import com.arkivanov.mvikotlin.core.lifecycle.resume
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener

class TimeTravelToolWindowListener(
    project: Project
) : ToolWindowManagerListener {

    private val toolWindowManager = ToolWindowManager.getInstance(project)

    override fun stateChanged() {
        if (TimeTravelToolWindowFactory.TOOL_WINDOW_ID in toolWindowManager.toolWindowIds) {
            ensureLifecycleRegistry().resume()
        } else {
            lifecycleRegistry?.destroy()
            lifecycleRegistry = null
        }
    }

    companion object {
        private var lifecycleRegistry: LifecycleRegistry? = null

        fun getLifecycle(): Lifecycle = ensureLifecycleRegistry()

        private fun ensureLifecycleRegistry(): LifecycleRegistry {
            if (lifecycleRegistry == null) {
                lifecycleRegistry = LifecycleRegistry()
            }

            return lifecycleRegistry!!
        }
    }
}
