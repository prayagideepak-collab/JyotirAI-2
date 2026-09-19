package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM indian_cities WHERE cityName LIKE :query OR historicalNames LIKE :query ORDER BY isPreferred DESC, cityName ASC")
    fun searchCities(query: String): Flow<List<CityEntity>>

    @Query("SELECT * FROM indian_cities")
    suspend fun getAllCities(): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)

    @Query("SELECT COUNT(*) FROM indian_cities")
    suspend fun getCityCount(): Int
}
