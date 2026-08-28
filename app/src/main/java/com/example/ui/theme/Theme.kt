package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SleekIndigoLight,
    onPrimary = SleekSlate900,
    primaryContainer = SleekIndigoContainerDark,
    onPrimaryContainer = SleekIndigoLight,
    secondary = SleekViolet,
    onSecondary = SleekSlate900,
    secondaryContainer = SleekSlate800,
    onSecondaryContainer = SleekSlate200,
    tertiary = SleekRose,
    onTertiary = SleekSlate900,
    tertiaryContainer = SleekIndigoContainerDark,
    onTertiaryContainer = SleekIndigoLight,
    background = SleekBackgroundDark,
    onBackground = SleekOnSurfaceDark,
    surface = SleekSurfaceDark,
    onSurface = SleekOnSurfaceDark,
    surfaceVariant = SleekSurfaceVariantDark,
    onSurfaceVariant = SleekOnSurfaceVariantDark,
    outline = SleekSlate700,
    outlineVariant = SleekSlate800
)

private val LightColorScheme = lightColorScheme(
    primary = SleekIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = SleekIndigoContainer,
    onPrimaryContainer = SleekOnIndigoContainer,
    secondary = SleekSlate700,
    onSecondary = Color.White,
    secondaryContainer = SleekSlate100,
    onSecondaryContainer = SleekSlate800,
    tertiary = SleekViolet,
    onTertiary = Color.White,
    tertiaryContainer = SleekVioletContainer,
    onTertiaryContainer = SleekViolet,
    background = SleekBackgroundLight,
    onBackground = SleekOnSurfaceLight,
    surface = SleekSurfaceLight,
    onSurface = SleekOnSurfaceLight,
    surfaceVariant = SleekSurfaceVariantLight,
    onSurfaceVariant = SleekOnSurfaceVariantLight,
    outline = SleekSlate400,
    outlineVariant = SleekSlate200
)

@Composable
fun BayanGouyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Persian brand colors prioritized
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
