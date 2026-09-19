package com.example.data

import android.content.Context
import com.example.model.ProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileManager private constructor(context: Context) {
    private val database = JyotishDatabase.getDatabase(context)
    private val repository = ProfileRepository(database.profileDao())
    private val scope = CoroutineScope(Dispatchers.IO)

    val profilesFlow = repository.allProfiles

    val activeProfileFlow: StateFlow<ProfileEntity?> = repository.allProfiles
        .map { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() }
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val _activeProfileId = MutableStateFlow<Long?>(null)
    val activeProfileId: StateFlow<Long?> = _activeProfileId

    init {
        scope.launch {
            activeProfileFlow.collect { profile ->
                _activeProfileId.value = profile?.id
            }
        }
    }

    suspend fun createProfile(
        name: String,
        birthDate: String,
        birthTime: String,
        birthPlace: String,
        latitude: Double,
        longitude: Double,
        timezone: String = "Asia/Kolkata"
    ): Boolean {
        return try {
            val count = repository.getProfileCount()
            if (count >= 3) return false
            val profile = ProfileEntity(
                name = name,
                birthDate = birthDate,
                birthTime = birthTime,
                birthPlace = birthPlace,
                latitude = latitude,
                longitude = longitude,
                timezone = timezone,
                isDefault = count == 0
            )
            repository.insertProfile(profile)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun switchActiveProfile(id: Long) {
        repository.setActiveProfile(id)
        _activeProfileId.value = id
    }

    suspend fun deleteProfile(profile: ProfileEntity) {
        repository.deleteProfile(profile)
    }

    suspend fun checkAndInvalidateCacheIfNeeded(
        existingProfile: ProfileEntity,
        newDob: String,
        newTime: String,
        newPlace: String
    ): Boolean {
        val changed = repository.hasBirthDataChanged(existingProfile, newDob, newTime, newPlace)
        if (changed) {
            // Trigger re-calculations or invalidate astrology cache
        }
        return changed
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfileManager? = null

        fun getInstance(context: Context): ProfileManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
