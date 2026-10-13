package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun OpenCodeTheme(
    appThemeType: AppThemeType = AppThemeType.CATPPUCCIN_MOCHA,
    isOledMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val activeType = if (isOledMode) AppThemeType.OLED_BLACK else appThemeType
    val colors = AppThemes.getColors(activeType)

    val colorScheme = darkColorScheme(
        primary = colors.primary,
        onPrimary = colors.background,
        primaryContainer = colors.surfaceHighlight,
        onPrimaryContainer = colors.textPrimary,
        secondary = colors.secondary,
        onSecondary = colors.background,
        secondaryContainer = colors.surfaceVariant,
        onSecondaryContainer = colors.textPrimary,
        tertiary = colors.tertiary,
        onTertiary = colors.background,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.surface,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.surfaceVariant,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.surfaceHighlight,
        error = Color(0xFFFF5555),
        onError = Color.White
    )

    CompositionLocalProvider(LocalAppThemeColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
