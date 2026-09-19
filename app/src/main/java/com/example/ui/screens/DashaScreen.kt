package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileManager
import com.example.engine.DetailedDashaPeriod
import com.example.engine.DashaCalculatorService
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashaScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val profileManager = remember { ProfileManager.getInstance(context) }
    val activeProfile by profileManager.activeProfileFlow.collectAsState(initial = null)

    if (activeProfile?.name.equals("Example", ignoreCase = true)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("विंशोत्तरी महादशा (Dasha)") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Example", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SaffronPrimary)
                        Text(
                            text = "यह केवल उदाहरण के लिए है। इस प्रोफाइल पर कोई व्यक्तिगत गणना नहीं होगी।",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        return
    }

    val dashaList = remember(activeProfile) {
        DashaCalculatorService.calculateVimshottariDasha(activeProfile)
    }

    var expandedPlanet by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("विंशोत्तरी महादशा (Vimshottari Dasha)") },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = "Timeline", tint = SaffronPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("120-वर्षीय जीवन चक्र (Vimshottari Life-Cycle)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "प्रोफाइल: ${activeProfile?.name ?: "–"} (जन्म: ${activeProfile?.birthDate ?: "–"})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Visual Timeline Component mapping Vimshottari Dasha chronologically
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("दशा काल क्रोनोलॉजिकल टाइमलाइन (Chronological Timeline)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Scroll horizontally to explore past, present, and future planetary periods.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))

                        val scrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .horizontalScroll(scrollState)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            dashaList.forEach { dasha ->
                                val isCurrent = dasha.isCurrent
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(110.dp)
                                        .background(
                                            if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(if (isCurrent) SaffronPrimary else MaterialTheme.colorScheme.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dasha.planet.take(2),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = dasha.planet,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${dasha.totalYears} Years",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${dasha.startDate.take(4)} - ${dasha.endDate.take(4)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isCurrent) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SaffronPrimary
                                        ) {
                                            Text("Active", fontSize = 8.sp, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(dashaList) { dasha ->
                val isExpanded = expandedPlanet == dasha.planet
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedPlanet = if (isExpanded) null else dasha.planet
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dasha.isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(dasha.planet, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    if (dasha.isCurrent) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SaffronPrimary
                                        ) {
                                            Text("Current (वर्तमान)", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("अवधि: ${dasha.startDate} — ${dasha.endDate} (${dasha.totalYears} वर्ष)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(
                                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand"
                            )
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("महत्वपूर्ण प्रभाव:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SaffronPrimary)
                            Text(dasha.significance, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("ज्योतिषीय उपाय (Remedy):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SaffronPrimary)
                            Text(dasha.remedy, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("अंतर्दशा (Antardashas):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            dasha.antardashas.forEach { sub ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${sub.planet}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("${sub.startDate} - ${sub.endDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

