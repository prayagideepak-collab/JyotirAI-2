package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SaffronPrimary

@Composable
fun MapPinDropDialog(
    initialLat: Double,
    initialLon: Double,
    onLocationConfirmed: (Double, Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLat by remember { mutableStateOf(initialLat) }
    var selectedLon by remember { mutableStateOf(initialLon) }
    var pinOffset by remember { mutableStateOf(Offset(200f, 200f)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("मानचित्र पर पिन ड्रॉप करें (Map Pin Drop)", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("नक्शे पर टैप करके सटीक जन्म स्थान (Lat/Lon) चुनें:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                // Interactive Canvas Map Simulator representing India coordinates region
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                pinOffset = offset
                                // Map canvas tap to approximate India latitude (8°N to 37°N) and longitude (68°E to 97°E)
                                val normX = (offset.x / size.width).coerceIn(0f, 1f)
                                val normY = (offset.y / size.height).coerceIn(0f, 1f)
                                selectedLon = 68.0 + (normX * 29.0) // 68 to 97
                                selectedLat = 35.0 - (normY * 26.0) // 35 to 9
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw grid lines
                        val stepX = size.width / 5
                        val stepY = size.height / 5
                        for (i in 1..4) {
                            drawLine(Color.Gray.copy(alpha = 0.3f), Offset(i * stepX, 0f), Offset(i * stepX, size.height), 1f)
                            drawLine(Color.Gray.copy(alpha = 0.3f), Offset(0f, i * stepY), Offset(size.width, i * stepY), 1f)
                        }
                    }

                    // Pin Marker
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(x = (pinOffset.x - 100).dp, y = (pinOffset.y - 110).dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = SaffronPrimary, modifier = Modifier.size(36.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Color.Black.copy(alpha = 0.7f)) {
                            Text(
                                "Lat: ${String.format("%.2f", selectedLat)}, Lon: ${String.format("%.2f", selectedLon)}",
                                fontSize = 9.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text("चयनित निर्देशांक: Lat ${String.format("%.4f", selectedLat)}, Lon ${String.format("%.4f", selectedLon)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLocationConfirmed(selectedLat, selectedLon, "नक्शा पिन स्थान (${String.format("%.2f", selectedLat)}, ${String.format("%.2f", selectedLon)})")
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("स्थान पुष्टि करें (Confirm)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}
