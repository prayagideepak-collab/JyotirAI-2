package com.example.engine

import com.example.model.DashaPeriod
import com.example.model.PanchangData
import com.example.model.PersonalizedPrediction
import com.example.model.NumberCompatibilityResult
import com.example.model.YogaDosha
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sin

object AstrologyCalculator {

    fun getPanchang(dateStr: String): PanchangData {
        return getPanchangForLocation(25.4358, 81.8463, dateStr)
    }

    fun getPanchangForLocation(lat: Double, lon: Double, dateStr: String): PanchangData {
        // Adjust sunrise/sunset slightly based on longitude offset from IST meridian (82.5 E)
        val offsetMinutes = ((lon - 82.5) * 4).toInt()
        val baseSunriseHour = 6
        val baseSunriseMin = 12 + offsetMinutes
        val sunriseHour = (baseSunriseHour + (baseSunriseMin / 60)).coerceIn(5, 7)
        val sunriseMin = (baseSunriseMin % 60).let { if (it < 0) it + 60 else it }

        val sunsetHour = 18
        val sunsetMin = 35 + offsetMinutes
        val actualSunsetHour = (sunsetHour + (sunsetMin / 60)).coerceIn(17, 19)
        val actualSunsetMin = (sunsetMin % 60).let { if (it < 0) it + 60 else it }

        // Brahma Muhurta starts 1 muhurta (48 mins) * 2 = 96 minutes before sunrise
        val totalSunriseMins = sunriseHour * 60 + sunriseMin
        val brahmaStartMins = totalSunriseMins - 96
        val brahmaEndMins = totalSunriseMins - 48

        val formatTime = { mins: Int ->
            val h = (mins / 60 + 24) % 24
            val m = mins % 60
            val suffix = if (h >= 12) "PM" else "AM"
            val displayH = if (h > 12) h - 12 else if (h == 0) 12 else h
            String.format("%02d:%02d %s", displayH, m, suffix)
        }

        val sunriseStr = String.format("%02d:%02d AM", sunriseHour, sunriseMin)
        val sunsetStr = String.format("%02d:%02d PM", actualSunsetHour, actualSunsetMin)
        val brahmaStr = "${formatTime(brahmaStartMins)} - ${formatTime(brahmaEndMins)}"

        return PanchangData(
            tithi = "Shukla Saptami (शुक सप्तमी)",
            nakshatra = "Rohini (रोहिणी) - 24°15'",
            yoga = "Siddha (सिद्ध) - Auspicious",
            karana = "Bhadra (विष्टी)",
            sunrise = sunriseStr,
            sunset = sunsetStr,
            brahmaMuhurta = brahmaStr,
            rahuKalam = "04:30 PM - 06:00 PM",
            auspiciousMuhurta = "Abhijit Muhurta: 11:45 AM - 12:35 PM",
            vedicDate = "Vikram Samvat 2083, Ashadha Shukla Paksha"
        )
    }

    fun getPersonalizedPrediction(profile: com.example.model.ProfileEntity?, date: String): PersonalizedPrediction? {
        if (profile == null || profile.isExample) {
            return null
        }
        return getPersonalizedPrediction(profile.id, profile.name, date)
    }

    fun getPersonalizedPrediction(profileId: Long, profileName: String, date: String): PersonalizedPrediction {
        val hash = abs((profileName + date).hashCode())
        val overviews = listOf(
            "आज का दिन आपके लिए नई ऊर्जा और सकारात्मकता लेकर आया है। महत्वपूर्ण निर्णय लेने के लिए समय उत्तम है।",
            "कार्यक्षेत्र में चुनौतियों का सामना करना पड़ सकता है, परंतु धैर्य और संयम से सब अनुकूल हो जाएगा।",
            "धन लाभ और आर्थिक उन्नति के योग बन रहे हैं। पारिवारिक जीवन में मधुरता बनी रहेगी।"
        )
        val careers = listOf(
            "सहकर्मियों का सहयोग मिलेगा। प्रमोशन या नई जिम्मेदारी मिलने की संभावना है.",
            "व्यापार में बड़ा निवेश करने से पहले अनुभवी व्यक्ति की सलाह अवश्य लें.",
            "विद्यार्थियों और शोधकर्ताओं के लिए समय अत्यधिक अनुकूल है। सफलता मिलेगी."
        )
        val finances = listOf(
            "अचानक धन प्राप्ति के योग हैं। पुराने कर्ज से मुक्ति मिल सकती है.",
            "खर्चों पर नियंत्रण रखें। अनावश्यक खरीदारी से बचें.",
            "संपत्ति या शेयर बाजार में निवेश लाभकारी सिद्ध हो सकता है."
        )
        val healths = listOf(
            "स्वास्थ्य उत्तम रहेगा। योग और प्राणायाम से मानसिक शांति मिलेगी.",
            "खान-पान का विशेष ध्यान रखें, पेट संबंधी छोटी-मोटी समस्या हो सकती है.",
            "पर्याप्त नींद लें और तरोताजा महसूस करें."
        )
        val relationships = listOf(
            "जीवनसाथी के साथ समय बिताने का अवसर मिलेगा। प्रेम संबंध प्रगाढ़ होंगे.",
            "परिवार में किसी मांगलिक कार्य की रूपरेखा बन सकती है.",
            "मित्रों के सहयोग से पुराने मनमुटाव दूर होंगे."
        )

        return PersonalizedPrediction(
            profileId = profileId,
            date = date,
            overview = overviews[hash % overviews.size],
            career = careers[(hash / 2) % careers.size],
            finance = finances[(hash / 3) % finances.size],
            health = healths[(hash / 4) % healths.size],
            relationships = relationships[(hash / 5) % relationships.size],
            favorableActivities = listOf("महत्वपूर्ण मीटिंग", "नया निवेश", "धार्मिक अनुष्ठान"),
            cautionAreas = listOf("वाहन सावधानी से चलाएं", "अतिउत्साه में निर्णय न लें")
        )
    }

    fun checkNumberCompatibility(numberStr: String, profileNumber: Int): NumberCompatibilityResult {
        val digitsOnly = numberStr.filter { it.isDigit() }
        val sum = digitsOnly.sumOf { it.toString().toInt() }
        var reduced = sum
        while (reduced > 9) {
            reduced = reduced.toString().sumOf { it.toString().toInt() }
        }

        val diff = abs(reduced - profileNumber)
        val level = when {
            diff == 0 || diff == 3 || diff == 6 -> "Highly Compatible"
            diff == 1 || diff == 2 || diff == 5 -> "Compatible"
            diff == 4 || diff == 7 -> "Neutral"
            else -> "Needs Consideration"
        }

        val explanation = "यह अंक (Reduced: $reduced) आपके मूलांक/भाग्यांक ($profileNumber) के साथ $level संबंध रखता है। अंक ज्योतिष के नियमों के अनुसार ऊर्जा का यह संतुलन आपके लिए अनुकूल है।"

        return NumberCompatibilityResult(
            enteredNumber = numberStr,
            reducedNumber = reduced,
            profileNumber = profileNumber,
            compatibilityLevel = level,
            explanation = explanation
        )
    }

    fun getDashaList(): List<DashaPeriod> {
        return listOf(
            DashaPeriod("Sun (सूर्य)", "2018-05-12", "2024-05-12", false),
            DashaPeriod("Moon (चन्द्र)", "2024-05-12", "2034-05-12", true),
            DashaPeriod("Mars (मंगल)", "2034-05-12", "2041-05-12", false),
            DashaPeriod("Rahu (राहु)", "2041-05-12", "2059-05-12", false),
            DashaPeriod("Jupiter (गुरु)", "2059-05-12", "2075-05-12", false),
            DashaPeriod("Saturn (शनि)", "2075-05-12", "2094-05-12", false)
        )
    }

    fun getYogaDoshaList(): List<YogaDosha> {
        return listOf(
            YogaDosha(
                name = "Gajakesari Yoga",
                type = "Yoga",
                description = "Jupiter is in a quadrant from the Moon. Grants immense wisdom, leadership, and wealth.",
                remedy = "Perform Yellow Sapphire charity on Thursdays.",
                isPresent = true
            ),
            YogaDosha(
                name = "Manglik Dosha (Kuja Dosha)",
                type = "Dosha",
                description = "Mars is placed in the 7th house from Lagna. May cause marital friction if unmitigated.",
                remedy = "Perform Mangal Shanti puja or Kumbh Vivah before marriage.",
                isPresent = true
            ),
            YogaDosha(
                name = "Budhaditya Yoga",
                type = "Yoga",
                description = "Sun and Mercury conjunction in 10th house. Excellent intellect, communication, and career success.",
                remedy = "Wear emerald or offer water to Surya daily.",
                isPresent = true
            ),
            YogaDosha(
                name = "Kaal Sarp Dosha",
                type = "Dosha",
                description = "All major planets are hemmed between Rahu and Ketu axis.",
                remedy = "Perform Maha Mrityunjaya Jaap and Kaal Sarp Shanti Puja at Trimbakeshwar.",
                isPresent = false
            )
        )
    }

    fun calculateCompatibility(boyName: String, girlName: String): Int {
        val combined = (boyName.length + girlName.length) * 3
        return 22 + (combined % 13)
    }

    fun getNumerology(name: String, dob: String): Pair<Int, Int> {
        val rawNum = name.uppercase().filter { it in 'A'..'Z' }.sumOf { (it - 'A') % 9 + 1 } % 9
        val nameNumber = if (rawNum == 0) 9 else rawNum
        val dobNumber = dob.filter { it.isDigit() }.sumOf { it.toString().toInt() }.let { sum ->
            var s = sum
            while (s > 9) {
                s = s.toString().sumOf { it.toString().toInt() }
            }
            s
        }
        return Pair(nameNumber, dobNumber)
    }

    val rashifalMap: Map<String, String> = mapOf(
        "Mesh (मेष - Aries)" to "आज का दिन करियर में नई ऊँचाइयाँ छूने का है। पारिवारिक जीवन में मधुरता बनी रहेगी। स्वास्थ्य उत्तम रहेगा।",
        "Vrishabh (वृषभ - Taurus)" to "धन लाभ के योग बन रहे हैं। निवेश करने से पहले बड़ों की सलाह अवश्य लें। यात्रा शुभ रहेगी।",
        "Mithun (मिथुन - Gemini)" to "विद्यार्थियों के लिए समय अनुकूल है। मित्रों का सहयोग मिलेगा। मान-सम्मान में वृद्धि होगी।",
        "Kark (कर्क - Cancer)" to "मानसिक शांति बनी रहेगी। व्यापार में नए अवसर मिलेंगे। खान-पान का ध्यान रखें।",
        "Simha (सिंह - Leo)" to "आत्मविश्वास में वृद्धि होगी। कार्यक्षेत्र में अधिकारी आपके काम से प्रसन्न रहेंगे। आर्थिक स्थिति मजबूत होगी।",
        "Kanya (कन्या - Virgo)" to "कड़ी मेहनत का फल मिलेगा। दांपत्य जीवन में खुशहाली आएगी। लंबी यात्रा के योग हैं।",
        "Tula (तुला - Libra)" to "रचनात्मक कार्यों में रुचि बढ़ेगी। पुराने मित्रों से मुलाकात होगी। स्वास्थ्य को लेकर सतर्क रहें।",
        "Vrischika (वृश्चिक - Scorpio)" to "गुप्त शत्रुओं से सावधान रहें। आर्थिक मामलों में सावधानी बरतें। परिवार का सहयोग संबल देगा।",
        "Dhanu (धनु - Sagittarius)" to "धार्मिक कार्यों में मन लगेगा। उच्च शिक्षा के क्षेत्र में सफलता मिलेगी। भाग्य का पूरा साथ मिलेगा।",
        "Makar (mकर - Capricorn)" to "कार्यक्षेत्र में जिम्मेदारियाँ बढ़ सकती हैं। धैर्य से काम लें। परिश्रम का फल अवश्य मिलेगा।",
        "Kumbh (कुंभ - Aquarius)" to "नई योजनाएँ सफल होंगी। आय के नए स्रोत बनेंगे। प्रेम संबंधों में प्रगाढ़ता आएगी।",
        "Meen (मीन - Pisces)" to "व्यापार में अचानक धन लाभ हो सकता है। स्वास्थ्य में सुधार होगा। मन प्रसन्न रहेगा।"
    )
}
