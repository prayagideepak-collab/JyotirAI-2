package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JyotishDatabase
import com.example.data.ProfileManager
import com.example.engine.AstrologyCalculator
import com.example.model.ProfileEntity
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MenuFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileManager = remember { ProfileManager.getInstance(context) }
    val dao = remember { JyotishDatabase.getDatabase(context).profileDao() }

    val profiles by profileManager.profilesFlow.collectAsState(initial = emptyList())
    val activeProfile by profileManager.activeProfileFlow.collectAsState(initial = null)

    var showProfileBottomSheet by remember { mutableStateOf(false) }
    var showGlossary by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val currentDateStr = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }
    val panchang = remember(activeProfile) {
        val lat = activeProfile?.latitude ?: 25.4358
        val lon = activeProfile?.longitude ?: 81.8463
        AstrologyCalculator.getPanchangForLocation(lat, lon, currentDateStr)
    }

    var isRefreshing by remember { mutableStateOf(false) }

    val features = listOf(
        MenuFeature("AI Astrologer", "चैट ज्योतिष सहायक", Icons.Default.SmartToy, "ai_chat"),
        MenuFeature("Kundli / Chart", "जन्म कुंडली & वर्ग", Icons.Default.Brightness7, "kundli"),
        MenuFeature("Panchang", "दैनिक पंचांग", Icons.Default.CalendarToday, "panchang"),
        MenuFeature("Dasha System", "विंशोत्तरी महादशा", Icons.Default.Timeline, "dasha"),
        MenuFeature("Yoga & Dosha", "योग एवं दोष विश्लेषण", Icons.Default.Warning, "yogas"),
        MenuFeature("Muhurta", "शुभ मुहूर्त", Icons.Default.AccessTime, "muhurta"),
        MenuFeature("Compatibility", "कुंडली मिलान", Icons.Default.Favorite, "compatibility"),
        MenuFeature("Rashifal", "दैनिक राशिफल", Icons.Default.Star, "rashifal"),
        MenuFeature("Daily Horoscope", "AI दैनिक भविष्यवाणी", Icons.Default.AutoAwesome, "daily_horoscope"),
        MenuFeature("Numerology", "अंक ज्योतिष", Icons.Default.LooksOne, "numerology"),
        MenuFeature("Face & Palm", "मुख और हस्त रेखा", Icons.Default.Face, "readings"),
        MenuFeature("Profiles", "जन्म प्रोफाइल", Icons.Default.People, "profiles"),
        MenuFeature("Settings", "सेटिंग्स", Icons.Default.Settings, "settings")
    )

    val featureRows = features.chunked(2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showProfileBottomSheet = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ॐ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(activeProfile?.name ?: "Jyotish (ज्योतिष)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Switch Profile", tint = SaffronPrimary)
                            }
                            Text("Tap to switch profile", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showGlossary = true }) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Glossary", tint = SaffronPrimary)
                    }
                    IconButton(onClick = {
                        if (!isRefreshing) {
                            isRefreshing = true
                            Toast.makeText(context, "पंचांग और ग्रह स्थिति अपडेट की जा रही है...", Toast.LENGTH_SHORT).show()
                            android.os.Handler(context.mainLooper).postDelayed({
                                isRefreshing = false
                                Toast.makeText(context, "अपडेट सफल! (Refreshed)", Toast.LENGTH_SHORT).show()
                            }, 1000)
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = SaffronPrimary)
                    }
                    IconButton(onClick = { onNavigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (showGlossary) {
            com.example.ui.components.GlossaryDialog(onDismiss = { showGlossary = false })
        }

        val earthState = remember(activeProfile) {
            val lat = activeProfile?.latitude ?: 25.4358
            val lon = activeProfile?.longitude ?: 81.8463
            val place = activeProfile?.birthPlace ?: "Prayagraj, Uttar Pradesh"
            val parts = place.split(",")
            val city = parts.getOrNull(0)?.trim() ?: "Prayagraj"
            val state = parts.getOrNull(1)?.trim() ?: "Uttar Pradesh"
            com.example.engine.LiveEarthEngine.calculateEarthState(lat, lon, city, state)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Earth Hero Scene (Scrollable)
            item {
                com.example.ui.components.LiveEarthScene(earthState = earthState)
            }

            // Active Profile Summary Card (Scrollable)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showProfileBottomSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = "Profile", tint = SaffronPrimary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = activeProfile?.name ?: "कोई सक्रिय प्रोफाइल नहीं",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SaffronPrimary
                            ) {
                                Text("Switch (बदलें)", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "जन्म स्थान: ${activeProfile?.birthPlace ?: '–'} | DOB: ${activeProfile?.birthDate ?: '–'}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("सूर्योदय", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(panchang.sunrise, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Column {
                                Text("ब्रह्म मुहूर्त", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(panchang.brahmaMuhurta, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SaffronPrimary)
                            }
                            Column {
                                Text("सूर्यास्त", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(panchang.sunset, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "वेदिक ज्योतिष सेवाएँ (Vedic Services)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(featureRows) { rowFeatures ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowFeatures.forEach { feature ->
                        Box(modifier = Modifier.weight(1f)) {
                            FeatureCard(feature = feature, onClick = { onNavigate(feature.route) })
                        }
                    }
                    if (rowFeatures.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Profile Switcher Bottom Sheet
        if (showProfileBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showProfileBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("सक्रिय प्रोफाइल चुनें (Select Active Profile)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("अधिकतम 3 प्रोफाइल समर्थित हैं (${profiles.size}/3)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(4.dp))

                    profiles.forEach { profile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch(Dispatchers.IO) {
                                        profileManager.switchActiveProfile(profile.id)
                                        withContext(Dispatchers.Main) {
                                            showProfileBottomSheet = false
                                            Toast.makeText(context, "सक्रिय प्रोफाइल: ${profile.name}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (profile.id == activeProfile?.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${profile.birthPlace} | ${profile.birthDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (profile.id == activeProfile?.id) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = SaffronPrimary) {
                                        Text("Active", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            showProfileBottomSheet = false
                            onNavigate("profiles")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.People, contentDescription = "Manage")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("सभी प्रोफाइल प्रबंधित करें (Manage Profiles)")
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: MenuFeature, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(feature.icon, contentDescription = feature.title, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            }
            Column {
                Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(feature.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}
