package com.arkivanov.mvikotlin.timetravel.client.internal.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier
) {
    androidx.compose.foundation.VerticalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier
    )
}

@Composable
internal actual fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier
) {
    androidx.compose.foundation.HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier
    )
}

