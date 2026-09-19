package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AstrologyCalculator
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(onBack: () -> Unit) {
    var boyName by remember { mutableStateOf("Rahul") }
    var girlName by remember { mutableStateOf("Priya") }
    var score by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("कुंडली मिलान (Gun Milan)") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("वर और वधू की जन्म जानकारी (Ashtakoot Milan)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = boyName,
                        onValueChange = { boyName = it },
                        label = { Text("वर का नाम (Boy's Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = girlName,
                        onValueChange = { girlName = it },
                        label = { Text("वधू का नाम (Girl's Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            score = AstrologyCalculator.calculateCompatibility(boyName, girlName)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Text("मिलान करें (Calculate Guna Milan)", fontSize = 14.sp)
                    }
                }
            }

            if (score != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("कुल गुण मिलान (Total Gunas)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("$score / 36", fontWeight = FontWeight.Bold, fontSize = 42.sp, color = SaffronPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        val verdict = if (score!! >= 18) "उत्तम मिलान (Compatible Match)" else "मध्यम मिलान (Moderate Match)"
                        Text(verdict, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("नाड़ी, भकूट और ग्रह मैत्री का मिलान सफल रहा।", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}
