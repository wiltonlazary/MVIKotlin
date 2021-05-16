package com.arkivanov.mvikotlin.timetravel.client.internal.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGreenColorPalette =
    darkColors(
        primary = green200,
        primaryVariant = green700,
        secondary = teal200,
        onPrimary = Color.Black,
        onSecondary = Color.White,
        error = Color.Red,
    )

private val LightGreenColorPalette =
    lightColors(
        primary = green500,
        primaryVariant = green700,
        secondary = teal200,
        onPrimary = Color.White,
        onSurface = Color.Black
    )

@Composable
fun TimeTravelClientTheme(
    darkTheme: Boolean = false,
    colorsOverride: (Colors) -> Colors = { it },
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colorsOverride(if (darkTheme) DarkGreenColorPalette else LightGreenColorPalette),
        content = content
    )
}
