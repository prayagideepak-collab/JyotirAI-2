package com.example.engine

import android.content.Context
import com.example.model.ProfileEntity
import java.util.TimeZone

data class ResolvedAstroContext(
    val latitude: Double,
    val longitude: Double,
    val timezone: TimeZone,
    val localDateTimeMillis: Long,
    val utcInstant: Long,
    val cityName: String,
    val stateName: String
)

object AstroContextManager {
    fun resolveContext(profile: ProfileEntity?): ResolvedAstroContext {
        val lat = profile?.latitude ?: 25.4358
        val lon = profile?.longitude ?: 81.8463
        val place = profile?.birthPlace ?: "Prayagraj, Uttar Pradesh"
        val parts = place.split(",")
        val city = parts.getOrNull(0)?.trim() ?: "Prayagraj"
        val state = parts.getOrNull(1)?.trim() ?: "Uttar Pradesh"
        
        val tz = TimeZone.getDefault()
        val now = System.currentTimeMillis()

        return ResolvedAstroContext(
            latitude = lat,
            longitude = lon,
            timezone = tz,
            localDateTimeMillis = now,
            utcInstant = now,
            cityName = city,
            stateName = state
        )
    }
}
