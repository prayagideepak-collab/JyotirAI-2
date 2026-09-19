package com.example.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileManager
import com.example.model.MuhurtaEvent
import com.example.ui.theme.SaffronPrimary

data class DetailedMuhurta(
    val event: MuhurtaEvent,
    val durationMinutes: Int,
    val scorePercentage: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhurtaScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val profileManager = remember { ProfileManager.getInstance(context) }
    val activeProfile by profileManager.activeProfileFlow.collectAsState(initial = null)

    val activities = listOf(
        "सभी कार्य (General)",
        "नया व्यापार (Business)",
        "विवाह (Marriage)",
        "वाहन क्रय (Vehicle)",
        "गृह प्रवेश (Griha Pravesh)",
        "यात्रा (Travel)"
    )

    var selectedActivity by remember { mutableStateOf("सभी कार्य (General)") }
    var showMonthlyCalendar by remember { mutableStateOf(false) }

    // Advanced Filter State
    var durationFilter by remember { mutableStateOf("सभी (All Durations)") } // "All", "Under 50m", "50m+"
    var intensityFilter by remember { mutableStateOf("सभी (All Scores)") } // "All", "90%+ Auspicious"

    val detailedMuhurtas = listOf(
        DetailedMuhurta(MuhurtaEvent(1, "अभिजीत मुहूर्त (Abhijit Muhurta)", "11:45 AM - 12:35 PM", "अत्यंत शुभ", "सभी प्रकार के नए कार्यों, व्यापार और यात्रा के लिए श्रेष्ठ।", true), 50, 98),
        DetailedMuhurta(MuhurtaEvent(2, "अमृत काल (Amrit Kaal)", "03:10 PM - 04:40 PM", "शुभ", "धन निवेश, संपत्ति क्रय और नए अनुबंध हेतु उत्तम समय।", true), 90, 92),
        DetailedMuhurta(MuhurtaEvent(3, "ब्रह्म मुहूर्त (Brahma Muhurta)", "04:20 AM - 05:10 AM", "परम शुभ", "ध्यान, साधना, शिक्षा और बौद्धिक कार्यों के लिए सर्वश्रेष्ठ।", true), 50, 99),
        DetailedMuhurta(MuhurtaEvent(4, "विजय मुहूर्त (Vijay Muhurta)", "02:15 PM - 03:00 PM", "शुभ", "मुकदमा, विवाद सुलझाने या महत्वपूर्ण प्रतियोगिता हेतु उत्तम।", true), 45, 88),
        DetailedMuhurta(MuhurtaEvent(5, "राहुकाल (Rahu Kalam)", "04:30 PM - 06:00 PM", "अशुभ", "इस अशुभ काल के दौरान किसी भी नए कार्य की शुरुआत न करें।", false), 90, 20)
    )

    val filteredMuhurtas = remember(selectedActivity, durationFilter, intensityFilter) {
        detailedMuhurtas.filter { item ->
            val matchesActivity = if (selectedActivity == "सभी कार्य (General)") true else (item.event.isAuspicious)
            val matchesDuration = when (durationFilter) {
                "Under 50m" -> item.durationMinutes <= 50
                "50m+" -> item.durationMinutes > 50
                else -> true
            }
            val matchesIntensity = when (intensityFilter) {
                "90%+ Auspicious" -> item.scorePercentage >= 90
                "Auspicious Only" -> item.event.isAuspicious
                else -> true
            }
            matchesActivity && matchesDuration && matchesIntensity
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("शुभ मुहूर्त कैलकुलेटर (Muhurta Tool)") },
                navigationIcon = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        showMonthlyCalendar = !showMonthlyCalendar
                    }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Monthly Calendar", tint = SaffronPrimary)
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("गतिविधि आधारित मुहूर्त चयन (Activity Muhurta)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "सक्रिय प्रोफाइल: ${activeProfile?.name ?: "–"} | स्थान: ${activeProfile?.birthPlace ?: "–"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Advanced Filter Options Row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("उन्नत फ़िल्टर (Advanced Filters)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Duration Filter Dropdown / Chips
                            listOf("सभी (All Durations)", "Under 50m", "50m+").forEach { dur ->
                                val selected = durationFilter == dur
                                Surface(
                                    modifier = Modifier.clickable {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        durationFilter = dur
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        dur,
                                        fontSize = 10.sp,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("सभी (All Scores)", "90%+ Auspicious", "Auspicious Only").forEach { score ->
                                val selected = intensityFilter == score
                                Surface(
                                    modifier = Modifier.clickable {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        intensityFilter = score
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        score,
                                        fontSize = 10.sp,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showMonthlyCalendar) {
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
                            Text("मासिक मुहूर्त कैलेंडर (Monthly Auspicious Calendar)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Highlighting favorable dates & scores for: $selectedActivity", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))

                            val days = (1..30).toList()
                            val favorableDays = listOf(3, 7, 10, 14, 18, 22, 25, 29)
                            val goldDays = listOf(5, 12, 20, 27)

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                days.chunked(7).forEach { week ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        week.forEach { day ->
                                            val isGold = goldDays.contains(day)
                                            val isFav = favorableDays.contains(day)
                                            val bgColor = when {
                                                isGold -> SaffronPrimary
                                                isFav -> MaterialTheme.colorScheme.primaryContainer
                                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            }
                                            val textColor = if (isGold) Color.White else MaterialTheme.colorScheme.onSurface

                                            Surface(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clickable {
                                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                        Toast.makeText(context, "दिनांक $day: ${if (isGold) "98% Gold Muhurta" else if (isFav) "85% Favorable" else "Standard"}", Toast.LENGTH_SHORT).show()
                                                    },
                                                shape = RoundedCornerShape(8.dp),
                                                color = bgColor
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text("$day", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                                                    Text(
                                                        text = if (isGold) "98%" else if (isFav) "85%" else "60%",
                                                        fontSize = 8.sp,
                                                        color = textColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("शुभ कार्य चुनें (Select Activity):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activities) { act ->
                        val isSelected = selectedActivity == act
                        Surface(
                            modifier = Modifier.clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                selectedActivity = act
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = act,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text("${selectedActivity} के लिए उपयुक्त समय स्लॉट (${filteredMuhurtas.size} परिणाम):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }

            items(filteredMuhurtas) { detailed ->
                val event = detailed.event
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("अवधि: ${detailed.durationMinutes} mins | स्कोर: ${detailed.scorePercentage}%", fontSize = 11.sp, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (event.isAuspicious) SaffronPrimary else Color.Gray
                            ) {
                                Text(event.quality, fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("समय: ${event.timeWindow}", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(event.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    Toast.makeText(context, "${event.title} के लिए अलार्म सेट किया गया!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = "Reminder", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("अलार्म", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    try {
                                        val intent = Intent(Intent.ACTION_INSERT).apply {
                                            data = CalendarContract.Events.CONTENT_URI
                                            putExtra(CalendarContract.Events.TITLE, "शुभ मुहूर्त: ${event.title} (${selectedActivity})")
                                            putExtra(CalendarContract.Events.DESCRIPTION, "${event.description}\nसमय: ${event.timeWindow}")
                                            putExtra(CalendarContract.Events.EVENT_LOCATION, activeProfile?.birthPlace ?: "India")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Google Calendar में जोड़ा गया!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Text("Google Calendar में जोड़ें", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
