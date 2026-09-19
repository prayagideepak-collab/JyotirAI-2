package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileManager
import com.example.engine.GeminiAiHelper
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyHoroscopeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileManager = remember { ProfileManager.getInstance(context) }
    val activeProfile by profileManager.activeProfileFlow.collectAsState(initial = null)

    if (activeProfile?.name.equals("Example", ignoreCase = true)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("दैनिक फलादेश (Horoscope)") },
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

    var horoscopeText by remember { mutableStateOf("दैनिक फलादेश लोड हो रहा है...") }
    var isLoading by remember { mutableStateOf(true) }

    fun loadHoroscope() {
        val name = activeProfile?.name ?: "साधक"
        val place = activeProfile?.birthPlace ?: "नई दिल्ली"
        val dob = activeProfile?.birthDate ?: "01/01/1995"
        isLoading = true
        scope.launch {
            val result = GeminiAiHelper.getDailyHoroscope(name, place, dob)
            withContext(Dispatchers.Main) {
                horoscopeText = result
                isLoading = false
            }
        }
    }

    LaunchedEffect(activeProfile) {
        loadHoroscope()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("दैनिक राशिफल (Gemini AI Daily Horoscope)") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { loadHoroscope() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = SaffronPrimary)
                    }
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaffronPrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = androidx.compose.ui.graphics.Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(activeProfile?.name ?: "प्रोफाइल", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Gemini 3.5 Flash द्वारा व्यक्तिगत दैनिक भविष्यवाणी", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        if (isLoading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = SaffronPrimary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("नक्षत्रों की गणना और AI विश्लेषण जारी है...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            Text(
                                text = horoscopeText,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
