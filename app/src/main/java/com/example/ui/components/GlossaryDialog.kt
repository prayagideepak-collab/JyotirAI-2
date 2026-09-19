package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SaffronPrimary

data class GlossaryTerm(
    val termEn: String,
    val termHi: String,
    val category: String,
    val definition: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryDialog(onDismiss: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    val terms = listOf(
        GlossaryTerm("Tithi", "तिथि", "Panchang", "A lunar day or the angular distance between the Sun and Moon (12 degrees). There are 30 Tithis in a lunar month."),
        GlossaryTerm("Nakshatra", "नक्षत्र", "Astronomy", "Lunar mansions or constellations along the ecliptic. There are 27 Nakshatras in Vedic astrology, each spanning 13°20'."),
        GlossaryTerm("Yoga (Panchang)", "योग (पंचांग)", "Panchang", "The sum of the motion of the Sun and Moon. There are 27 Nitya Yogas, each influencing auspiciousness of activities."),
        GlossaryTerm("Karana", "करण", "Panchang", "Half of a Tithi. There are 11 Karanas in total (4 fixed and 7 revolving) used for muhurta selection."),
        GlossaryTerm("Vimshottari Dasha", "विंशोत्तरी महादशा", "Predictive", "A 120-year planetary period system based on the Moon's Nakshatra at birth, dictating life chapters."),
        GlossaryTerm("Lagna", "लग्न (Ascendant)", "Chart", "The rising sign on the eastern horizon at the exact time and place of birth, representing the Self and body."),
        GlossaryTerm("Bhava", "भाव (House)", "Chart", "One of the 12 astrological houses in a birth chart, each governing specific life domains (wealth, career, relationships)."),
        GlossaryTerm("Navamsha (D9)", "नवमांश चार्ट (D9)", "Divisional", "The 9th harmonic chart derived from Lagna, representing soul purpose, marriage, and second half of life."),
        GlossaryTerm("Rahu Kalam", "राहु काल", "Muhurta", "An inauspicious period of approximately 1.5 hours daily ruled by Rahu, avoided for starting new ventures."),
        GlossaryTerm("Manglik Dosha", "मांगलिक दोष", "Dosha", "Astrological condition when Mars is placed in 1st, 4th, 7th, 8th, or 12th house from Lagna/Moon."),
        GlossaryTerm("Sade Sati", "साढ़े साती", "Transit", "The 7.5-year transit period of Saturn over the natal Moon sign and adjacent signs, bringing karmic lessons."),
        GlossaryTerm("Gaja Kesari Yoga", "गजकेसरी योग", "Yoga", "A powerful benefic yoga formed when Jupiter is in a Kendra (1, 4, 7, 10) from the Moon, granting wisdom and fame."),
        GlossaryTerm("Budhaditya Yoga", "बुधादित्य योग", "Yoga", "A conjunction of Sun and Mercury in a house, bestowing high intellect, sharp communication, and success."),
        GlossaryTerm("Raj Yoga", "राज योग", "Yoga", "Combinations involving lords of Kendra and Trikona houses that bring leadership, power, and prosperity."),
        GlossaryTerm("Pitru Dosha", "पितृ दोष", "Dosha", "Formed by afflicted Sun, Saturn, or Rahu in 9th house or connected to Pitru markers, requiring ancestral remedies."),
        GlossaryTerm("Kaal Sarp Dosha", "कालसर्प दोष", "Dosha", "Formed when all 7 planets are hemmed between Rahu and Ketu, creating intense life struggles before breakthroughs."),
        GlossaryTerm("Ashtakavarga", "अष्टकवर्ग", "Predictive", "A quantitative point system in Vedic astrology evaluating strength of transits and planets across houses."),
        GlossaryTerm("Hora", "होरा", "Muhurta", "An hour-long planetary period division of the day, utilized for fine-tuning hourly auspicious tasks.")
    )

    val filteredTerms = remember(searchQuery) {
        if (searchQuery.isBlank()) terms
        else terms.filter {
            it.termEn.contains(searchQuery, true) ||
            it.termHi.contains(searchQuery, true) ||
            it.definition.contains(searchQuery, true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.85f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ज्योतिष शब्दावली (Vedic Glossary)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("शब्दावली खोजें (Search terms...)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTerms) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${item.termEn} (${item.termHi})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(item.category, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Text(item.definition, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("बंद करें (Close)")
            }
        }
    )
}
