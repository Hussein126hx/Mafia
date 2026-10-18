package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MafiaPixelColorScheme = darkColorScheme(
    primary = MafiaGoldAccent,
    onPrimary = MafiaBgNearBlack,
    primaryContainer = MafiaBronzeBorder,
    onPrimaryContainer = TextCreamPrimary,
    secondary = ActionBlueHighlight,
    onSecondary = MafiaBgNearBlack,
    tertiary = ActionRedHighlight,
    onTertiary = TextCreamPrimary,
    background = MafiaBgNearBlack,
    onBackground = TextCreamPrimary,
    surface = MafiaSurfaceDark,
    onSurface = TextCreamPrimary,
    surfaceVariant = MafiaSurfaceElevated,
    onSurfaceVariant = TextCreamSecondary,
    outline = MafiaGoldAccent,
    outlineVariant = MafiaBronzeBorder
)

@Composable
fun MafiaPixelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MafiaPixelColorScheme,
        typography = Typography,
        content = content
    )
}
