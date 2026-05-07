package com.ivy.movie.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2563EB),
    secondary = Color(0xFF0F766E),
    tertiary = Color(0xFFB45309),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB91C1C),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF93C5FD),
    secondary = Color(0xFF5EEAD4),
    tertiary = Color(0xFFFCD34D),
    background = Color(0xFF111827),
    surface = Color(0xFF1F2937),
    error = Color(0xFFFCA5A5),
)

@Composable
fun MovieTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
