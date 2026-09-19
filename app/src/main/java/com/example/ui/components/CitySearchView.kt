package com.example.ui.components

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CityEntity
import com.example.ui.theme.SaffronPrimary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchView(
    query: String,
    onQueryChanged: (String) -> Unit,
    suggestions: List<CityEntity>,
    onCitySelected: (CityEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                val spokenText = results[0]
                onQueryChanged(spokenText)
                expanded = true
            }
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChanged(it)
                expanded = true
            },
            label = { Text("जन्म स्थान खोजें (Voice / Search City)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary) },
            trailingIcon = {
                IconButton(onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "शहर का नाम बोलें (Speak City Name)...")
                    }
                    try {
                        speechRecognizerLauncher.launch(intent)
                    } catch (e: Exception) {
                        // Handle if speech recognition not available
                    }
                }) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = SaffronPrimary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (expanded && suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                LazyColumn {
                    items(suggestions) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCitySelected(city)
                                    expanded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationCity, contentDescription = "City", tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(city.cityName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (city.historicalNames.isNotBlank()) {
                                        Text("पूर्व नाम (Formerly): ${city.historicalNames}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            Text(city.state, fontSize = 11.sp, color = SaffronPrimary)
                        }
                    }
                }
            }
        }
    }
}
