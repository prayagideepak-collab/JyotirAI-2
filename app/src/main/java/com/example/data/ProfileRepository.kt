package com.example.data

import com.example.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val profileDao: ProfileDao) {
    val allProfiles: Flow<List<ProfileEntity>> = profileDao.getAllProfiles()

    suspend fun getProfileCount(): Int = profileDao.getProfileCount()

    suspend fun insertProfile(profile: ProfileEntity): Long {
        val count = profileDao.getProfileCount()
        if (count >= 3) {
            throw IllegalStateException("Maximum 3 persistent user profiles allowed.")
        }
        val isFirst = count == 0
        val newProfile = profile.copy(isDefault = isFirst || profile.isDefault)
        if (newProfile.isDefault) {
            profileDao.clearDefaultFlags()
        }
        return profileDao.insertProfile(newProfile)
    }

    suspend fun deleteProfile(profile: ProfileEntity) {
        profileDao.deleteProfile(profile)
    }

    suspend fun setActiveProfile(id: Long) {
        profileDao.clearDefaultFlags()
        profileDao.setDefaultProfile(id)
    }

    suspend fun getProfileById(id: Long): ProfileEntity? = profileDao.getProfileById(id)

    /**
     * Helper method in ProfileRepository to check if birth data (DOB, Time, Place)
     * has changed to trigger an invalidation of the associated astrology results cache.
     */
    fun hasBirthDataChanged(
        oldProfile: ProfileEntity,
        newBirthDate: String,
        newBirthTime: String,
        newBirthPlace: String
    ): Boolean {
        return oldProfile.birthDate != newBirthDate ||
               oldProfile.birthTime != newBirthTime ||
               oldProfile.birthPlace != newBirthPlace
    }
}
