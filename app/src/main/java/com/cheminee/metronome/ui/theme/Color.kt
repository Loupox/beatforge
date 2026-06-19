package com.cheminee.metronome.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object BeatForgeColors {

    object BronzeCharbon {
        val background = Color(0xFF181210)
        val surface = Color(0xFF231b14)
        val surface2 = Color(0xFF2d2318)
        val topbar = Color(0xFF0f0d0a)
        val border = Color(0xFF3a2a1a)
        val textPrimary = Color(0xFFf0e6d3)
        val textMuted = Color(0xFF8a7460)
        val textDim = Color(0xFF4a3d2e)
        val accent = Color(0xFFcd7f32)
        val accentLight = Color(0xFFe8a654)
        val accentDark = Color(0xFF8b5520)
    }

    object BoisClair {
        val background = Color(0xFFf5efe6)
        val surface = Color(0xFFede3d5)
        val surface2 = Color(0xFFe0d4c4)
        val topbar = Color(0xFFd9cbb8)
        val border = Color(0xFFc4b49e)
        val textPrimary = Color(0xFF2a1f12)
        val textMuted = Color(0xFF7a6248)
        val textDim = Color(0xFFb09878)
        val accent = Color(0xFF8b4a0a)
        val accentLight = Color(0xFFc06810)
        val accentDark = Color(0xFFa85c12)
    }
}

val DarkColors = darkColorScheme(
    primary = BeatForgeColors.BronzeCharbon.accent,
    onPrimary = BeatForgeColors.BronzeCharbon.background,
    primaryContainer = BeatForgeColors.BronzeCharbon.surface2,
    onPrimaryContainer = BeatForgeColors.BronzeCharbon.accentLight,
    secondary = BeatForgeColors.BronzeCharbon.accentLight,
    onSecondary = BeatForgeColors.BronzeCharbon.background,
    secondaryContainer = BeatForgeColors.BronzeCharbon.surface2,
    onSecondaryContainer = BeatForgeColors.BronzeCharbon.accentLight,
    tertiary = BeatForgeColors.BronzeCharbon.accentDark,
    onTertiary = BeatForgeColors.BronzeCharbon.textPrimary,
    background = BeatForgeColors.BronzeCharbon.background,
    onBackground = BeatForgeColors.BronzeCharbon.textPrimary,
    surface = BeatForgeColors.BronzeCharbon.topbar,
    onSurface = BeatForgeColors.BronzeCharbon.textPrimary,
    surfaceVariant = BeatForgeColors.BronzeCharbon.surface,
    onSurfaceVariant = BeatForgeColors.BronzeCharbon.textMuted,
    outline = BeatForgeColors.BronzeCharbon.border,
    outlineVariant = BeatForgeColors.BronzeCharbon.border,
    error = Color(0xFFff6b6b),
    onError = Color(0xFF1a0a0a)
)

val LightColors = lightColorScheme(
    primary = BeatForgeColors.BoisClair.accent,
    onPrimary = Color(0xFFf5efe6),
    primaryContainer = BeatForgeColors.BoisClair.surface2,
    onPrimaryContainer = BeatForgeColors.BoisClair.accent,
    secondary = BeatForgeColors.BoisClair.accentLight,
    onSecondary = BeatForgeColors.BoisClair.background,
    secondaryContainer = BeatForgeColors.BoisClair.surface2,
    onSecondaryContainer = BeatForgeColors.BoisClair.accent,
    tertiary = BeatForgeColors.BoisClair.accentDark,
    onTertiary = BeatForgeColors.BoisClair.background,
    background = BeatForgeColors.BoisClair.background,
    onBackground = BeatForgeColors.BoisClair.textPrimary,
    surface = BeatForgeColors.BoisClair.topbar,
    onSurface = BeatForgeColors.BoisClair.textPrimary,
    surfaceVariant = BeatForgeColors.BoisClair.surface,
    onSurfaceVariant = BeatForgeColors.BoisClair.textMuted,
    outline = BeatForgeColors.BoisClair.border,
    outlineVariant = BeatForgeColors.BoisClair.border,
    error = Color(0xFFc0392b),
    onError = Color(0xFFf5efe6)
)