package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettingsView.Model

@Composable
fun Settings(view: TimeTravelSettingsViewProxy) {
    val model = view.models.value ?: return

    Settings(
        model = model,
        events = SettingsEvents(
            onCancel = { view.dispatch(Event.CancelClicked) },
            onSave = { view.dispatch(Event.SaveClicked) },
            onHostChanged = { view.dispatch(Event.HostChanged(it)) },
            onPortChanged = { view.dispatch(Event.PortChanged(it)) },
            onWrapEventDetailsChanged = { view.dispatch(Event.WrapEventDetailsChanged(it)) }
        )
    )
}

@Composable
private fun Settings(
    model: Model,
    events: SettingsEvents
) {
    model.editing?.also {
        SettingsDialog(
            editing = it,
            events = events
        )
    }
}

@Composable
private fun SettingsDialog(
    editing: Model.Editing,
    events: SettingsEvents
) {
    PopupDialog(title = "Settings", onDismissRequest = events.onCancel) {
        Column(modifier = Modifier.padding(16.dp).width(IntrinsicSize.Min)) {
            TextField(
                value = editing.host,
                onValueChange = events.onHostChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Host") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = editing.port,
                onValueChange = events.onPortChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Port") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { events.onWrapEventDetailsChanged(!editing.wrapEventDetails) }
            ) {
                Checkbox(
                    checked = editing.wrapEventDetails,
                    onCheckedChange = events.onWrapEventDetailsChanged
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Wrap event details")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(Alignment.End)) {
                Button(onClick = events.onCancel) {
                    Text(text = "Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = events.onSave) {
                    Text(text = "Save")
                }
            }
        }
    }
}

private class SettingsEvents(
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
    val onHostChanged: (String) -> Unit,
    val onPortChanged: (String) -> Unit,
    val onWrapEventDetailsChanged: (Boolean) -> Unit
)
