package com.example.engine

import androidx.compose.ui.graphics.Color

enum class SolarThemeMode {
    DAY, TWILIGHT, NIGHT
}

data class InterpolatedThemeColors(
    val primary: Color,
    val surface: Color,
    val cardBackground: Color,
    val background: Color,
    val onSurface: Color,
    val modeName: String
)

object JyotirAIThemeController {
    fun getThemeColors(solarElevation: Float): InterpolatedThemeColors {
        return when {
            solarElevation > 5f -> InterpolatedThemeColors(
                primary = Color(0xFFFFB300), // Celestial Gold
                surface = Color(0xFF0F172A),
                cardBackground = Color(0xFF1E293B),
                background = Color(0xFF060B19),
                onSurface = Color(0xFFF8FAFC),
                modeName = "Day (दिन)"
            )
            solarElevation in -6f..5f -> InterpolatedThemeColors(
                primary = Color(0xFFFF9800), // Twilight Amber
                surface = Color(0xFF111827),
                cardBackground = Color(0xFF1F2937),
                background = Color(0xFF0B0F19),
                onSurface = Color(0xFFF3F4F6),
                modeName = "Twilight (संध्या)"
            )
            else -> InterpolatedThemeColors(
                primary = Color(0xFFFFC107), // Night Gold
                surface = Color(0xFF090D16),
                cardBackground = Color(0xFF0F172A),
                background = Color(0xFF04060F),
                onSurface = Color(0xFFE2E8F0),
                modeName = "Night (रात्रि)"
            )
        }
    }
}
