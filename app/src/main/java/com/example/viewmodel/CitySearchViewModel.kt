package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JyotishDatabase
import com.example.model.CityEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CitySearchViewModel(application: Application) : AndroidViewModel(application) {
    private val cityDao = JyotishDatabase.getDatabase(application).cityDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<CityEntity>> = _searchQuery
        .debounce(150)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                cityDao.searchCities("%%")
            } else {
                cityDao.searchCities("%$query%")
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (cityDao.getCityCount() == 0) {
                val initialCities = listOf(
                    CityEntity(cityName = "Prayagraj", historicalNames = "Allahabad, Alhabad", state = "Uttar Pradesh", latitude = 25.4358, longitude = 81.8463),
                    CityEntity(cityName = "Mumbai", historicalNames = "Bombay, Bambai", state = "Maharashtra", latitude = 19.0760, longitude = 72.8777),
                    CityEntity(cityName = "Chennai", historicalNames = "Madras", state = "Tamil Nadu", latitude = 13.0827, longitude = 80.2707),
                    CityEntity(cityName = "Bengaluru", historicalNames = "Bangalore", state = "Karnataka", latitude = 12.9716, longitude = 77.5946),
                    CityEntity(cityName = "Kolkata", historicalNames = "Calcutta", state = "West Bengal", latitude = 22.5726, longitude = 88.3639),
                    CityEntity(cityName = "Pune", historicalNames = "Poona", state = "Maharashtra", latitude = 18.5204, longitude = 73.8567),
                    CityEntity(cityName = "Vadodara", historicalNames = "Baroda", state = "Gujarat", latitude = 22.3072, longitude = 73.1812),
                    CityEntity(cityName = "Kanpur", historicalNames = "Cawnpore", state = "Uttar Pradesh", latitude = 26.4499, longitude = 80.3319),
                    CityEntity(cityName = "Varanasi", historicalNames = "Banaras, Kashi", state = "Uttar Pradesh", latitude = 25.3176, longitude = 82.9739),
                    CityEntity(cityName = "Thiruvananthapuram", historicalNames = "Trivandrum", state = "Kerala", latitude = 8.5241, longitude = 76.9366),
                    CityEntity(cityName = "New Delhi", historicalNames = "Delhi, Dilli", state = "Delhi", latitude = 28.6139, longitude = 77.2090),
                    CityEntity(cityName = "Hyderabad", historicalNames = "Golconda", state = "Telangana", latitude = 17.3850, longitude = 78.4867),
                    CityEntity(cityName = "Ahmedabad", historicalNames = "Karnavati", state = "Gujarat", latitude = 23.0225, longitude = 72.5714),
                    CityEntity(cityName = "Jaipur", historicalNames = "Pink City", state = "Rajasthan", latitude = 26.9124, longitude = 75.7873),
                    CityEntity(cityName = "Ayodhya", historicalNames = "Faizabad", state = "Uttar Pradesh", latitude = 26.7922, longitude = 82.1998)
                )
                cityDao.insertCities(initialCities)
            }
        }
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
