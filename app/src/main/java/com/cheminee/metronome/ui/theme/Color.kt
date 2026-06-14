package com.cheminee.metronome.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object ChemineeColors {
    val Charbon = Color(0xFF3D3D3D)
    val Ivoire = Color(0xFFE8E4DF)
    val OrMat = Color(0xFFC4973A)
    val BlancChaud = Color(0xFFF7F4F0)
    val Noir = Color(0xFF111111)
    val SurfaceLight = Color(0xFFEDEAE5)
    val SurfaceDark = Color(0xFF1C1C1C)
}

val LightColors = lightColorScheme(
    primary = ChemineeColors.Charbon,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = ChemineeColors.SurfaceLight,
    onPrimaryContainer = ChemineeColors.Charbon,
    secondary = ChemineeColors.OrMat,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = ChemineeColors.OrMat.copy(alpha = 0.2f),
    onSecondaryContainer = ChemineeColors.OrMat,
    tertiary = ChemineeColors.OrMat,
    onTertiary = Color(0xFFFFFFFF),
    background = ChemineeColors.BlancChaud,
    onBackground = ChemineeColors.Charbon,
    surface = Color(0xFFFFFFFF),
    onSurface = ChemineeColors.Charbon,
    surfaceVariant = ChemineeColors.SurfaceLight,
    onSurfaceVariant = ChemineeColors.Charbon.copy(alpha = 0.7f),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF)
)

val DarkColors = darkColorScheme(
    primary = ChemineeColors.Ivoire,
    onPrimary = ChemineeColors.Noir,
    primaryContainer = ChemineeColors.SurfaceDark,
    onPrimaryContainer = ChemineeColors.Ivoire,
    secondary = ChemineeColors.OrMat,
    onSecondary = ChemineeColors.Noir,
    secondaryContainer = ChemineeColors.OrMat.copy(alpha = 0.3f),
    onSecondaryContainer = ChemineeColors.OrMat,
    tertiary = ChemineeColors.OrMat,
    onTertiary = ChemineeColors.Noir,
    background = ChemineeColors.Noir,
    onBackground = ChemineeColors.Ivoire,
    surface = ChemineeColors.SurfaceDark,
    onSurface = ChemineeColors.Ivoire,
    surfaceVariant = ChemineeColors.SurfaceDark,
    onSurfaceVariant = ChemineeColors.Ivoire.copy(alpha = 0.7f),
    error = Color(0xFFF2B8B5),
    onError = ChemineeColors.Noir
)