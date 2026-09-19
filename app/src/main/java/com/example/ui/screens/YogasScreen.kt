package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AstrologyCalculator
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogasScreen(onBack: () -> Unit) {
    val yogas = AstrologyCalculator.getYogaDoshaList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("योग एवं दोष विश्लेषण (Yogas & Doshas)") },
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
            items(yogas) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (item.type == "Yoga") SaffronPrimary else Color.Red.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = if (item.isPresent) "${item.type} (मौजूद)" else "${item.type} (अनुपस्थित)",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("उपाय (Remedy): ${item.remedy}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SaffronPrimary)
                    }
                }
            }
        }
    }
}
