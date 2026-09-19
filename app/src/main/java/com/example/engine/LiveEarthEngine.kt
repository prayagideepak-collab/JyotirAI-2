package com.example.engine

import java.util.Calendar
import java.util.TimeZone

data class LiveEarthState(
    val latitude: Double = 25.4358,
    val longitude: Double = 81.8463,
    val utcInstant: Long = System.currentTimeMillis(),
    val rotationAngle: Float = 0f,
    val subsolarLongitude: Float = 0f,
    val solarDeclination: Float = 0f,
    val isDay: Boolean = true,
    val localSolarHourAngle: Float = 0f,
    val solarElevation: Float = 0f,
    val dayNightState: String = "Day (दिन)",
    val cityName: String = "Prayagraj",
    val stateName: String = "Uttar Pradesh"
)

object LiveEarthEngine {
    fun calculateEarthState(lat: Double, lon: Double, cityName: String = "Prayagraj", stateName: String = "Uttar Pradesh"): LiveEarthState {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        val declination = 23.44 * kotlin.math.sin(Math.toRadians(((284 + dayOfYear) * 360 / 365.0)))
        val utcHours = hour + minute / 60.0 + second / 3600.0
        var subsolarLon = ((12.0 - utcHours) * 15.0).toFloat()
        if (subsolarLon > 180f) subsolarLon -= 360f
        if (subsolarLon < -180f) subsolarLon += 360f

        val rotation = ((utcHours / 24.0) * 360f).toFloat()
        val localHourAngle = subsolarLon - lon.toFloat()

        val latRad = Math.toRadians(lat)
        val decRad = Math.toRadians(declination)
        val hourAngleRad = Math.toRadians(localHourAngle.toDouble())
        val sinElevation = kotlin.math.sin(latRad) * kotlin.math.sin(decRad) +
                kotlin.math.cos(latRad) * kotlin.math.cos(decRad) * kotlin.math.cos(hourAngleRad)
        val solarElevation = Math.toDegrees(kotlin.math.asin(sinElevation.coerceIn(-1.0, 1.0))).toFloat()

        val isDayTime = solarElevation > -0.833f
        val dayNightStr = when {
            solarElevation > 5f -> "Day (दिन)"
            solarElevation in -6f..5f -> "Twilight (संध्या)"
            else -> "Night (रात्रि)"
        }

        return LiveEarthState(
            latitude = lat,
            longitude = lon,
            utcInstant = now,
            rotationAngle = rotation,
            subsolarLongitude = subsolarLon,
            solarDeclination = declination.toFloat(),
            isDay = isDayTime,
            localSolarHourAngle = localHourAngle,
            solarElevation = solarElevation,
            dayNightState = dayNightStr,
            cityName = cityName,
            stateName = stateName
        )
    }
}
