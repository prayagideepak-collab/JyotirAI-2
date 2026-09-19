package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CityEntity
import com.example.model.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY isDefault DESC, id DESC")
    fun getAllProfiles(): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): Profile?

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile): Long

    @Delete
    suspend fun deleteProfile(profile: Profile)

    @Query("UPDATE profiles SET isDefault = 0")
    suspend fun clearDefaultFlags()

    @Query("UPDATE profiles SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultProfile(id: Long)
}

@Database(entities = [Profile::class, CityEntity::class], version = 5, exportSchema = false)
abstract class JyotishDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: JyotishDatabase? = null

        fun getDatabase(context: Context): JyotishDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JyotishDatabase::class.java,
                    "jyotish_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
