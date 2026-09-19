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

@Composable
fun LiveEarthView(
    earthState: LiveEarthState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "EarthRotation")
    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = earthState.rotationAngle,
        targetValue = earthState.rotationAngle + 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(120000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAnim"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                            .background(if (earthState.isDay) Color(0xFFFFB300) else Color(0xFF00E5FF))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE EARTH ENGINE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                Text(earthState.dayNightState, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f - 12f

                    // 1. Star field background
                    drawCircle(color = Color(0xFF1E1B4B), radius = radius + 8f, center = center)

                    // 2. Zodiac Ring
                    drawCircle(
                        color = Color(0xFFFFD700).copy(alpha = 0.4f),
                        radius = radius + 6f,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                    )

                    // 3. Earth Body
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = radius,
                        center = center
                    )

                    // 4. Atmospheric Rim
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0xFF00E5FF).copy(alpha = 0.25f)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // 5. Continents / Night simulation
                    rotate(animatedRotation, pivot = center) {
                        drawCircle(
                            color = Color(0xFF334155).copy(alpha = 0.4f),
                            radius = radius * 0.75f,
                            center = center + Offset(15f, -10f)
                        )
                        drawCircle(
                            color = Color(0xFFFFB300).copy(alpha = 0.3f),
                            radius = radius * 0.5f,
                            center = center + Offset(-20f, 15f)
                        )
                    }

                    // 6. Day / Night Terminator
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                            startX = 0f,
                            endX = size.width
                        )
                    )

                    // 7. User Location Marker
                    val markerAngle = Math.toRadians((earthState.longitude + animatedRotation).toDouble())
                    val markerX = center.x + (radius * 0.6f * kotlin.math.cos(markerAngle)).toFloat()
                    val markerY = center.y + (radius * 0.4f * kotlin.math.sin(markerAngle)).toFloat()
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 5f,
                        center = Offset(markerX, markerY)
                    )
                    drawCircle(
                        color = Color(0xFFFF6F00),
                        radius = 2.5f,
                        center = Offset(markerX, markerY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${earthState.cityName}, ${earthState.stateName}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Lat: ${String.format("%.2f", earthState.latitude)}° | Lon: ${String.format("%.2f", earthState.longitude)}°",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
