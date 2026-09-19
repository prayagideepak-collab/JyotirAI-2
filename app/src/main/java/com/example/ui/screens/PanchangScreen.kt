package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileManager
import com.example.engine.AstrologyCalculator
import com.example.ui.theme.SaffronPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val profileManager = remember { ProfileManager.getInstance(context) }
    val activeProfile by profileManager.activeProfileFlow.collectAsState(initial = null)

    val currentDateStr = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }

    val panchang = remember(activeProfile) {
        val lat = activeProfile?.latitude ?: 25.4358
        val lon = activeProfile?.longitude ?: 81.8463
        AstrologyCalculator.getPanchangForLocation(lat, lon, currentDateStr)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("वास्तविक दैनिक पंचांग (Real-time Panchang)") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = SaffronPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(panchang.vedicDate, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("सक्रिय स्थान: ${activeProfile?.birthPlace ?: "New Delhi, India"} (Lat: ${String.format("%.2f", activeProfile?.latitude ?: 28.61)})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                }
            }

            val itemsList = listOf(
                Pair("तिथि (Tithi)", panchang.tithi),
                Pair("नक्षत्र (Nakshatra)", panchang.nakshatra),
                Pair("योग (Yoga)", panchang.yoga),
                Pair("करण (Karana)", panchang.karana),
                Pair("सूर्योदय / सूर्यास्त (Sunrise / Sunset)", "${panchang.sunrise} / ${panchang.sunset}"),
                Pair("ब्रह्म मुहूर्त (Brahma Muhurta)", panchang.brahmaMuhurta),
                Pair("राहुकाल (Rahu Kalam)", panchang.rahuKalam),
                Pair("शुभ मुहूर्त (Auspicious)", panchang.auspiciousMuhurta)
            )

            items(itemsList.size) { index ->
                val (title, value) = itemsList[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
