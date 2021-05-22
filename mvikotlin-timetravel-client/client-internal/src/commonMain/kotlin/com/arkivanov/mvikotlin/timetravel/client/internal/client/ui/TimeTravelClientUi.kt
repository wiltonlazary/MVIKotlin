package com.arkivanov.mvikotlin.timetravel.client.internal.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.PhonelinkOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient.Model
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.HorizontalScrollbar
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.PopupDialog
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.ToolbarButton
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.TreeNode
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.VerticalScrollbar
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.contentAlpha
import com.arkivanov.mvikotlin.timetravel.client.internal.compose.contentColor
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.ui.TimeTravelSettingsUi
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ParsedValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.toTreeNode

@Composable
fun TimeTravelClientUi(component: TimeTravelClient) {
    Box(modifier = Modifier.fillMaxSize()) {
        val model = component.models.value

        Client(
            model = model,
            wrapEventDetails = component.settings.models.value.settings.wrapEventDetails,
            buttonBarEvents = ButtonBarEvents(
                onConnect = component::onConnectClicked,
                onDisconnect = component::onDisconnectClicked,
                onStartRecording = component::onStartRecordingClicked,
                onStopRecording = component::onStopRecordingClicked,
                onMoveToStart = component::onMoveToStartClicked,
                onStepBackward = component::onStepBackwardClicked,
                onStepForward = component::onStepForwardClicked,
                onMoveToEnd = component::onMoveToEndClicked,
                onCancel = component::onCancelClicked,
                onDebug = component::onDebugEventClicked,
                onExportEvents = component::onExportEventsClicked,
                onImportEvents = component::onImportEventsClicked,
                onEditSettings = component::onEditSettingsClicked
            ),
            onEventClick = component::onEventSelected
        )

        TimeTravelSettingsUi(component.settings)

        model.errorText?.also {
            Error(
                error = it,
                onDismiss = component::onDismissErrorClicked
            )
        }
    }
}

@Composable
private fun Client(
    model: Model,
    wrapEventDetails: Boolean,
    buttonBarEvents: ButtonBarEvents,
    onEventClick: (Int) -> Unit
) {
    Column {
        ButtonBar(
            buttons = model.buttons,
            events = buttonBarEvents
        )

        Divider()

        Events(
            events = model.events,
            currentEventIndex = model.currentEventIndex,
            selectedEventIndex = model.selectedEventIndex,
            selectedEventValue = model.selectedEventValue,
            wrapEventDetails = wrapEventDetails,
            onClick = onEventClick
        )
    }
}

@Composable
private fun Error(error: String, onDismiss: () -> Unit) {
    PopupDialog(title = "Error", onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = error,
                modifier = Modifier.width(IntrinsicSize.Max).widthIn(max = 640.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Close")
            }
        }
    }
}

@Composable
private fun ButtonBar(
    buttons: Model.Buttons,
    events: ButtonBarEvents
) {
    Surface(modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxSize()) {
            ToolbarButton(
                imageVector = Icons.Default.Phonelink,
                enabled = buttons.isConnectEnabled,
                onClick = events.onConnect
            )
            ToolbarButton(
                imageVector = Icons.Default.PhonelinkOff,
                enabled = buttons.isDisconnectEnabled,
                onClick = events.onDisconnect
            )

            VerticalDivider()

            ToolbarButton(
                imageVector = Icons.Default.FiberManualRecord,
                enabled = buttons.isStartRecordingEnabled,
                onClick = events.onStartRecording
            )
            ToolbarButton(
                imageVector = Icons.Default.Stop,
                enabled = buttons.isStopRecordingEnabled,
                onClick = events.onStopRecording
            )
            ToolbarButton(
                imageVector = Icons.Default.SkipPrevious,
                enabled = buttons.isMoveToStartEnabled,
                onClick = events.onMoveToStart
            )
            ToolbarButton(
                imageVector = Icons.Default.ChevronLeft,
                enabled = buttons.isStepBackwardEnabled,
                onClick = events.onStepBackward
            )
            ToolbarButton(
                imageVector = Icons.Default.ChevronRight,
                enabled = buttons.isStepForwardEnabled,
                onClick = events.onStepForward
            )
            ToolbarButton(
                imageVector = Icons.Default.SkipNext,
                enabled = buttons.isMoveToEndEnabled,
                onClick = events.onMoveToEnd
            )
            ToolbarButton(
                imageVector = Icons.Default.Close,
                enabled = buttons.isCancelEnabled,
                onClick = events.onCancel
            )
            ToolbarButton(
                imageVector = Icons.Default.BugReport,
                enabled = buttons.isDebugEventEnabled,
                onClick = events.onDebug
            )

            VerticalDivider()

            ToolbarButton(
                imageVector = Icons.Default.Share,
                enabled = buttons.isExportEventsEnabled,
                onClick = events.onExportEvents
            )

            ToolbarButton(
                imageVector = Icons.Default.Download,
                enabled = buttons.isImportEventsEnabled,
                onClick = events.onImportEvents
            )

            VerticalDivider()

            ToolbarButton(
                imageVector = Icons.Default.Settings,
                onClick = events.onEditSettings
            )
        }
    }
}

@Composable
private fun Events(
    events: List<String>,
    currentEventIndex: Int,
    selectedEventIndex: Int,
    selectedEventValue: ParsedValue?,
    wrapEventDetails: Boolean,
    onClick: (Int) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        EventList(
            events = events,
            modifier = Modifier.weight(0.4F).fillMaxHeight(),
            currentEventIndex = currentEventIndex,
            selectedEventIndex = selectedEventIndex,
            onClick = onClick
        )

        VerticalDivider()

        EventDetails(
            value = selectedEventValue,
            wrap = wrapEventDetails,
            modifier = Modifier.weight(0.6F).fillMaxHeight()
        )
    }
}

@Composable
private fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F),
    thickness: Dp = 1.dp,
    indent: Dp = 4.dp
) {
    Box(
        modifier
            .padding(top = indent, bottom = indent)
            .fillMaxHeight()
            .width(thickness)
            .background(color = color)
    )
}

@Composable
private fun EventList(
    events: List<String>,
    modifier: Modifier,
    currentEventIndex: Int,
    selectedEventIndex: Int,
    onClick: (Int) -> Unit
) {
    Box(modifier = modifier) {
        val listState = rememberLazyListState()

        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            itemsIndexed(events) { index, event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(index) }
                        .run { if (index == selectedEventIndex) background(contentColor(alpha = 0.2F)) else this }
                ) {
                    Text(
                        text = event,
                        modifier = Modifier.padding(4.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = contentColor(alpha = contentAlpha().times(if (index <= currentEventIndex) 1F else 0.5F))
                    )
                }
            }
        }

        VerticalScrollbar(
            lazyListState = listState,
            itemCount = events.size,
            averageItemSize = 28.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(vertical = 8.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun EventDetails(
    value: ParsedValue?,
    wrap: Boolean,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        val scrollStateVertical = rememberScrollState(0)
        val scrollStateHorizontal = rememberScrollState(0)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollStateVertical)
                .run { if (wrap) this else horizontalScroll(scrollStateHorizontal) }
        ) {
            if (value != null) {
                TreeNode(
                    node = value.toTreeNode(),
                    title = { text ->
                        Text(
                            text = text,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    isInitiallyExpanded = true
                )
            }
        }

        VerticalScrollbar(
            scrollState = scrollStateVertical,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(vertical = 8.dp)
                .fillMaxHeight()
        )

        if (!wrap) {
            HorizontalScrollbar(
                scrollState = scrollStateHorizontal,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

private class ButtonBarEvents(
    val onConnect: () -> Unit,
    val onDisconnect: () -> Unit,
    val onStartRecording: () -> Unit,
    val onStopRecording: () -> Unit,
    val onMoveToStart: () -> Unit,
    val onStepBackward: () -> Unit,
    val onStepForward: () -> Unit,
    val onMoveToEnd: () -> Unit,
    val onCancel: () -> Unit,
    val onDebug: () -> Unit,
    val onExportEvents: () -> Unit,
    val onImportEvents: () -> Unit,
    val onEditSettings: () -> Unit
)
