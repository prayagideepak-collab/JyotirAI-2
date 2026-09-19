package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LiveEarthState
import com.example.engine.JyotirAIThemeController

@Composable
fun LiveEarthScene(
    earthState: LiveEarthState,
    modifier: Modifier = Modifier
) {
    val themeColors = remember(earthState.solarElevation) {
        JyotirAIThemeController.getThemeColors(earthState.solarElevation)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "EarthSceneRotation")
    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = earthState.rotationAngle,
        targetValue = earthState.rotationAngle + 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(140000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SceneRotationAnim"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(themeColors.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "LIVE EARTH & SOLAR TERMINATOR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = themeColors.primary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = themeColors.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = themeColors.modeName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f - 10f

                    // 1. Star field cosmic aura
                    drawCircle(color = Color(0xFF0A0F1D), radius = radius + 10f, center = center)

                    // 2. Zodiac Ring with Golden Glow
                    drawCircle(
                        color = Color(0xFFFFD700).copy(alpha = 0.45f),
                        radius = radius + 6f,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.8f)
                    )

                    // 3. Earth Base Sphere
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = radius,
                        center = center
                    )

                    // 4. Atmospheric Blue/Cyan Rim
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0xFF00E5FF).copy(alpha = 0.28f)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // 5. Rotating Continents & City Lights representation
                    rotate(animatedRotation, pivot = center) {
                        drawCircle(
                            color = Color(0xFF334155).copy(alpha = 0.5f),
                            radius = radius * 0.72f,
                            center = center + Offset(16f, -12f)
                        )
                        // India / Asia highlight
                        drawCircle(
                            color = Color(0xFFFFB300).copy(alpha = 0.4f),
                            radius = radius * 0.35f,
                            center = center + Offset(-15f, 18f)
                        )
                    }

                    // 6. Day / Night Terminator Overlay
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = if (earthState.isDay) 0.3f else 0.75f), Color.Transparent),
                            startX = 0f,
                            endX = size.width
                        )
                    )

                    // 7. User Location Marker Pin
                    val markerAngle = Math.toRadians((earthState.longitude + animatedRotation).toDouble())
                    val markerX = center.x + (radius * 0.58f * kotlin.math.cos(markerAngle)).toFloat()
                    val markerY = center.y + (radius * 0.42f * kotlin.math.sin(markerAngle)).toFloat()
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 6f,
                        center = Offset(markerX, markerY)
                    )
                    drawCircle(
                        color = Color(0xFFFF6F00),
                        radius = 3f,
                        center = Offset(markerX, markerY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${earthState.cityName}, ${earthState.stateName}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = themeColors.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Lat: ${String.format("%.2f", earthState.latitude)}° | Lon: ${String.format("%.2f", earthState.longitude)}° | Solar Elev: ${String.format("%.1f", earthState.solarElevation)}°",
                fontSize = 11.sp,
                color = themeColors.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
