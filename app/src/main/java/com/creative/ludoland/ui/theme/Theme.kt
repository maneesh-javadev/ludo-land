package com.creative.ludoland.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LudoColorScheme = lightColorScheme(
    primary = LudoBlue,
    onPrimary = TextLight,
    primaryContainer = LudoBlueLight,
    onPrimaryContainer = LudoBlueDark,
    secondary = LudoYellow,
    onSecondary = TextPrimary,
    secondaryContainer = LudoYellowLight,
    onSecondaryContainer = LudoYellowDark,
    tertiary = LudoRed,
    onTertiary = TextLight,
    tertiaryContainer = LudoRedLight,
    onTertiaryContainer = LudoRedDark,
    background = GradientTop,
    onBackground = TextLight,
    surface = CreamTile,
    onSurface = TextPrimary,
    surfaceVariant = CreamTileDark,
    onSurfaceVariant = TextPrimary
)

@Composable
fun LudoLandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LudoColorScheme,
        typography = Typography,
        content = content
    )
}
