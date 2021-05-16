package com.arkivanov.mvikotlin.timetravel.client.internal.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
)

@Composable
internal expect fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
)

