package com.example.engine

import com.example.model.Profile
import java.text.SimpleDateFormat
import java.util.*

data class Antardasha(
    val planet: String,
    val startDate: String,
    val endDate: String
)

data class DetailedDashaPeriod(
    val planet: String,
    val lord: String,
    val totalYears: Int,
    val startDate: String,
    val endDate: String,
    val isCurrent: Boolean,
    val significance: String,
    val remedy: String,
    val antardashas: List<Antardasha>
)

object DashaCalculatorService {

    fun calculateVimshottariDasha(profile: Profile?): List<DetailedDashaPeriod> {
        if (profile == null || profile.isExample) {
            return emptyList()
        }
        // Base birth year from profile birthDate (DD/MM/YYYY) or default to 1995
        val birthYear = profile?.birthDate?.let { dateStr ->
            val parts = dateStr.split("/")
            if (parts.size == 3) parts[2].toIntOrNull() else 1995
        } ?: 1995

        val planets = listOf(
            Triple("Ketu (केतु)", 7, " espiritual growth, liberation, sudden changes"),
            Triple("Venus (शुक्र)", 20, "luxuries, relationships, arts, comforts"),
            Triple("Sun (सूर्य)", 6, "authority, vitality, career success, father"),
            Triple("Moon (चन्द्र)", 10, "mind, emotions, travel, public interaction"),
            Triple("Mars (मंगल)", 7, "courage, property, energy, leadership"),
            Triple("Rahu (राहु)", 18, "ambition, foreign travels, sudden gains"),
            Triple("Jupiter (गुरु)", 16, "wisdom, wealth, expansion, children"),
            Triple("Saturn (शनि)", 19, "discipline, hard work, karma, longevity"),
            Triple("Mercury (बुध)", 17, "intellect, business, communication, education")
        )

        var currentYear = birthYear + 5 // starting prime dasha offset
        val currentCalendarYear = Calendar.getInstance().get(Calendar.YEAR)

        return planets.mapIndexed { index, (planetName, years, significance) ->
            val startY = currentYear
            val endY = currentYear + years
            currentYear = endY

            val isCurr = currentCalendarYear in startY..endY

            val remedy = when {
                planetName.contains("Sun") -> "Offer water to Sun daily and recite Aditya Hridaya Stotra."
                planetName.contains("Moon") -> "Wear silver, worship Lord Shiva, and drink water in silver vessel."
                planetName.contains("Mars") -> "Donate red lentils on Tuesdays and feed stray dogs."
                planetName.contains("Mercury") -> "Wear emerald or feed green grass to cows on Wednesdays."
                planetName.contains("Jupiter") -> "Apply turmeric tilak and donate yellow items on Thursdays."
                planetName.contains("Venus") -> "Respect women, wear white/diamods, and use fragrant attar."
                planetName.contains("Saturn") -> "Donate black sesame oil on Saturdays and help the needy."
                planetName.contains("Rahu") -> "Feed birds with mixed grains and chant Durga Chalisa."
                else -> "Chant Ketu mantra 'Om Kem Ketave Namah' and donate blanket."
            }

            // Generate sample sub-periods (Antardashas)
            val antardashas = planets.take(4).map { sub ->
                Antardasha(
                    planet = sub.first,
                    startDate = "01/01/$startY",
                    endDate = "31/12/${startY + 2}"
                )
            }

            DetailedDashaPeriod(
                planet = planetName,
                lord = planetName.substringAfter("(").substringBefore(")"),
                totalYears = years,
                startDate = "15/06/$startY",
                endDate = "15/06/$endY",
                isCurrent = isCurr,
                significance = "Period of $significance. Focus on spiritual and material balance.",
                remedy = remedy,
                antardashas = antardashas
            )
        }
    }
}
