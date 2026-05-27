package com.hivestudio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

private val HiveStudioLightScheme = lightColorScheme(
    primary = BlueAccent,
    onPrimary = SurfaceText,
    secondary = BlueAccentSoft,
    background = Color(0xFFF6F7FB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF6B7280),
)

@Composable
fun HiveStudioTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) HiveStudioDarkScheme else HiveStudioLightScheme,
        typography = Typography,
        content = content,
    )
}
