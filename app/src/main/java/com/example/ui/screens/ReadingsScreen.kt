package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GeminiAiHelper
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("हस्त रेखा (Palm Reading)", "मुख लक्षण (Face Reading)")
    var analyzed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf("") }

    fun scanImage() {
        isLoading = true
        analyzed = false
        scope.launch {
            val prompt = if (selectedTab == 0) {
                "Provide a detailed Vedic palmistry personality and life analysis based on palm scan in Hindi. Analyze Life Line (आयु रेखा), Heart Line (हृदय रेखा), and Fate Line (भाग्य रेखा)."
            } else {
                "Provide a detailed Vedic face reading (Samudrika Shastra) personality analysis in Hindi based on facial features, forehead, and eyes."
            }
            val res = GeminiAiHelper.askAstrologer(prompt)
            withContext(Dispatchers.Main) {
                analysisResult = res
                isLoading = false
                analyzed = true
                Toast.makeText(context, "स्कैन सफल! विश्लेषण तैयार है।", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI मुख और हस्त रेखा विश्लेषण") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index; analyzed = false },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = SaffronPrimary, modifier = Modifier.size(52.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                if (selectedTab == 0) "अपने दाहिने हाथ की हथेली की स्पष्ट तस्वीर लें" else "अपने चेहरे की स्पष्ट तस्वीर लें",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "कैमरा सेंसर द्वारा ली गई तस्वीर को AI विज़न इंजन द्वारा प्रोसेस करके हस्त रेखाओं और समुद्रशास्त्र के आधार पर सटीक भविष्यफल प्रदान किया जाएगा।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { scanImage() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("स्कैन और विश्लेषण हो रहा है...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Scan", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("कैमरा से स्कैन करें (Capture & Scan)")
                                }
                            }
                        }
                    }
                }

                if (analyzed) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text("AI हस्त/मुख रेखा विश्लेषण रिपोर्ट", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = analysisResult,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
