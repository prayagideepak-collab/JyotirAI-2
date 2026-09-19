package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.NumberCompatibilityResult
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumerologyScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Prayagi Deepak") }
    var dob by remember { mutableStateOf("15/08/1995") }
    var numbers by remember { mutableStateOf(AstrologyCalculator.getNumerology("Prayagi Deepak", "15/08/1995")) }

    // Compatibility checker state
    var inputNumber by remember { mutableStateOf("") }
    var selectedNumberType by remember { mutableStateOf("Mobile") } // Mobile, Vehicle, House, Business
    var compatibilityResult by remember { mutableStateOf<NumberCompatibilityResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("अंक ज्योतिष एवं संख्या मिलान (Numerology)") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("व्यक्तिगत अंक गणना (Profile Numerology)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("पूरा नाम (Full Name)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = dob,
                            onValueChange = { dob = it },
                            label = { Text("जन्म तिथि (DD/MM/YYYY)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                numbers = AstrologyCalculator.getNumerology(name, dob)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("अंक गणना करें (Calculate)", fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("भाग्यांक (Destiny No.)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${numbers.first}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("मूलांक (Birth No.)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${numbers.second}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }
                }
            }

            // Number Compatibility Checker Section (Mobile, Vehicle, House, Business)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("संख्या अनुकूलता जाँच (Number Compatibility Checker)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("मोबाइल नंबर, गाड़ी नंबर, मकान नंबर या व्यवसायिक संख्या की अनुकूलता स्थानीय रूप से जांचें (Local Privacy).", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Mobile", "Vehicle", "House", "Business").forEach { type ->
                                OutlinedButton(
                                    onClick = { selectedNumberType = type },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (selectedNumberType == type) SaffronPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Text(type, fontSize = 11.sp, fontWeight = if (selectedNumberType == type) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = inputNumber,
                            onValueChange = { inputNumber = it },
                            label = { Text("संख्या दर्ज करें ($selectedNumberType Number)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (inputNumber.isNotBlank()) {
                                    compatibilityResult = AstrologyCalculator.checkNumberCompatibility(inputNumber, numbers.second)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("अनुकूलता जाँचें (Check Compatibility)", fontSize = 14.sp)
                        }

                        compatibilityResult?.let { res ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("परिणाम: ${res.compatibilityLevel}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("मूल अंक (Reduced): ${res.reducedNumber} | आपका मूलांक: ${res.profileNumber}", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(res.explanation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
