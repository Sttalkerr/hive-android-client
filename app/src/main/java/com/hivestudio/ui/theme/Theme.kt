package com.hivestudio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HiveStudioDarkScheme = darkColorScheme(
    primary = BlueAccent,
    onPrimary = SurfaceText,
    secondary = BlueAccentSoft,
    background = Graphite,
    onBackground = SurfaceText,
    surface = GraphiteSoft,
    onSurface = SurfaceText,
    onSurfaceVariant = SurfaceMuted,
)

@Composable
fun HiveStudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HiveStudioDarkScheme,
        typography = Typography,
        content = content,
    )
}
