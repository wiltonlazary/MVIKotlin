package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Action
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClientView.Model
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

class TimeTravelClientViewProxy : MviViewProxy<Model, Event>(), TimeTravelClientView {

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    override fun execute(action: Action): Unit =
        when (action) {
            is Action.ExportEvents -> exportEvents(data = action.data)
            is Action.ImportEvents -> importEvents()
            is Action.ShowError -> _error.value = action.text
        }

    fun onDismissError() {
        _error.value = null
    }

    private fun exportEvents(data: ByteArray) {
        val dialog = FileDialog(null as Frame?, "MVIKotlin time travel export", FileDialog.SAVE)
        dialog.file = "TimeTravelEvents.tte"
        dialog.isVisible = true

        dialog
            .selectedFile
            ?.writeBytes(data)
    }

    private fun importEvents() {
        val dialog = FileDialog(null as Frame?, "MVIKotlin time travel import", FileDialog.LOAD)
        dialog.filenameFilter = FilenameFilter { _, name -> name.endsWith(".tte") }
        dialog.isVisible = true

        val file = dialog.selectedFile ?: return
        val data = file.readBytes()
    }

    private val FileDialog.selectedFile: File?
        get() = if ((directory != null) && (file != null)) File(directory, file) else null
}
