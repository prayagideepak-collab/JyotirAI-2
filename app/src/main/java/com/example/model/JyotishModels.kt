package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val birthDate: String, // DD/MM/YYYY
    val birthTime: String, // HH:MM AM/PM
    val birthTimestamp: Long = System.currentTimeMillis(),
    val birthPlace: String,
    val latitude: Double = 25.4358, // default Prayagraj
    val longitude: Double = 81.8463,
    val timezone: String = "Asia/Kolkata",
    val gender: String = "Male",
    val isDefault: Boolean = false,
    val profileImageUri: String? = null,
    val isExample: Boolean = false,
    val isDemo: Boolean = false
)

typealias Profile = ProfileEntity

data class LocationData(
    val latitude: Double = 25.4358,
    val longitude: Double = 81.8463,
    val cityName: String = "Prayagraj, India",
    val timezone: String = "Asia/Kolkata",
    val accuracyMeters: Float = 10f,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class PanchangData(
    val tithi: String,
    val nakshatra: String,
    val yoga: String,
    val karana: String,
    val sunrise: String,
    val sunset: String,
    val brahmaMuhurta: String,
    val rahuKalam: String,
    val auspiciousMuhurta: String,
    val vedicDate: String
)

data class MuhurtaEvent(
    val id: Int,
    val title: String,
    val timeWindow: String,
    val quality: String,
    val description: String,
    val isAuspicious: Boolean
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class DashaPeriod(
    val planet: String,
    val startDate: String,
    val endDate: String,
    val isCurrent: Boolean
)

data class YogaDosha(
    val name: String,
    val type: String, // "Yoga" or "Dosha"
    val description: String,
    val remedy: String,
    val isPresent: Boolean
)

data class PersonalizedPrediction(
    val profileId: Long,
    val date: String,
    val overview: String,
    val career: String,
    val finance: String,
    val health: String,
    val relationships: String,
    val favorableActivities: List<String>,
    val cautionAreas: List<String>
)

data class NumberCompatibilityResult(
    val enteredNumber: String,
    val reducedNumber: Int,
    val profileNumber: Int,
    val compatibilityLevel: String, // "Highly Compatible", "Compatible", "Neutral", "Needs Consideration"
    val explanation: String
)

@Entity(tableName = "indian_cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val historicalNames: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String = "Asia/Kolkata",
    val isPreferred: Boolean = true
)
