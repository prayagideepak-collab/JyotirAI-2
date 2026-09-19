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

private val DarkColorScheme =
    darkColorScheme(
        primary = DarkPrimary,
        secondary = GoldAccent,
        tertiary = SaffronPrimary,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkCard,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

private val CelestialColorScheme =
    darkColorScheme(
        primary = Color(0xFFFFD700), // Celestial Gold
        secondary = Color(0xFF9C27B0), // Nebula Purple
        tertiary = Color(0xFF00E5FF), // Astral Cyan
        background = Color(0xFF0A0818), // Deep Cosmic Night
        surface = Color(0xFF141129), // Cosmic Card Surface
        surfaceVariant = Color(0xFF231F45),
        onPrimary = Color(0xFF0A0818),
        onSecondary = Color.White,
        onBackground = Color(0xFFE0E0E0),
        onSurface = Color(0xFFFFFFFF)
    )

private val LightColorScheme =
    lightColorScheme(
        primary = SaffronPrimary,
        secondary = GoldAccent,
        tertiary = VedicPurple,
        background = VedicSurface,
        surface = VedicCard,
        surfaceVariant = Color(0xFFFFECB3),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    celestialTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            celestialTheme -> CelestialColorScheme
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
