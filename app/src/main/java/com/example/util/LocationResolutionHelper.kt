package com.example.util

data class ResolvedLocation(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val isPreferredIndia: Boolean = true
)

object LocationResolutionHelper {
    // Comprehensive offline database of Indian and international cities, with India preferred.
    // Handles historical name mapping (e.g. Allahabad -> Prayagraj, Bombay -> Mumbai, etc.)
    private val historicalMap = mapOf(
        "allahabad" to "Prayagraj",
        "bombay" to "Mumbai",
        "madras" to "Chennai",
        "bangalore" to "Bengaluru",
        "calcutta" to "Kolkata",
        "poona" to "Pune",
        "baroda" to "Vadodara",
        "cawnpore" to "Kanpur",
        "trivandrum" to "Thiruvananthapuram",
        "gauhati" to "Guwahati",
        "waltair" to "Visakhapatnam",
        "banaras" to "Varanasi",
        "patiala" to "Patiala"
    )

    private val masterCityDatabase = listOf(
        ResolvedLocation("Prayagraj, India", 25.4358, 81.8463, "Asia/Kolkata", true),
        ResolvedLocation("New Delhi, India", 28.6139, 77.2090, "Asia/Kolkata", true),
        ResolvedLocation("Mumbai, India", 19.0760, 72.8777, "Asia/Kolkata", true),
        ResolvedLocation("Varanasi, India", 25.3176, 82.9739, "Asia/Kolkata", true),
        ResolvedLocation("Bengaluru, India", 12.9716, 77.5946, "Asia/Kolkata", true),
        ResolvedLocation("Kolkata, India", 22.5726, 88.3639, "Asia/Kolkata", true),
        ResolvedLocation("Chennai, India", 13.0827, 80.2707, "Asia/Kolkata", true),
        ResolvedLocation("Hyderabad, India", 17.3850, 78.4867, "Asia/Kolkata", true),
        ResolvedLocation("Ayodhya, India", 26.7922, 82.1998, "Asia/Kolkata", true),
        ResolvedLocation("Mathura, India", 27.4924, 77.6737, "Asia/Kolkata", true),
        ResolvedLocation("Haridwar, India", 29.9457, 78.1642, "Asia/Kolkata", true),
        ResolvedLocation("Rishikesh, India", 30.0869, 78.2676, "Asia/Kolkata", true),
        ResolvedLocation("Ujjain, India", 23.1793, 75.7849, "Asia/Kolkata", true),
        ResolvedLocation("Jaipur, India", 26.9124, 75.7873, "Asia/Kolkata", true),
        ResolvedLocation("Lucknow, India", 26.8467, 80.9462, "Asia/Kolkata", true),
        ResolvedLocation("Patna, India", 25.5941, 85.1376, "Asia/Kolkata", true),
        ResolvedLocation("Bhopal, India", 23.2599, 77.4126, "Asia/Kolkata", true),
        ResolvedLocation("Indore, India", 22.7196, 75.8577, "Asia/Kolkata", true),
        ResolvedLocation("Ahmedabad, India", 23.0225, 72.5714, "Asia/Kolkata", true),
        ResolvedLocation("Pune, India", 18.5204, 73.8567, "Asia/Kolkata", true),
        ResolvedLocation("Nagpur, India", 21.1458, 79.0882, "Asia/Kolkata", true),
        ResolvedLocation("Kanpur, India", 26.4499, 80.3319, "Asia/Kolkata", true),
        ResolvedLocation("Chandigarh, India", 30.7333, 76.7794, "Asia/Kolkata", true),
        ResolvedLocation("Amritsar, India", 31.6340, 74.8723, "Asia/Kolkata", true),
        ResolvedLocation("Gaya, India", 24.7913, 85.0002, "Asia/Kolkata", true),
        ResolvedLocation("Madurai, India", 9.9252, 78.1198, "Asia/Kolkata", true),
        ResolvedLocation("Tirupati, India", 13.6288, 79.4192, "Asia/Kolkata", true),
        ResolvedLocation("Ranchi, India", 23.3441, 85.3096, "Asia/Kolkata", true),
        ResolvedLocation("Raipur, India", 21.2514, 81.6296, "Asia/Kolkata", true),
        ResolvedLocation("Dehradun, India", 30.3165, 78.0322, "Asia/Kolkata", true),
        ResolvedLocation("Shimla, India", 31.1048, 77.1734, "Asia/Kolkata", true),
        ResolvedLocation("Srinagar, India", 34.0837, 74.7973, "Asia/Kolkata", true),
        ResolvedLocation("Jammu, India", 32.7266, 74.8570, "Asia/Kolkata", true),
        ResolvedLocation("Vadodara, India", 22.3072, 73.1812, "Asia/Kolkata", true),
        ResolvedLocation("Surat, India", 21.1702, 72.8311, "Asia/Kolkata", true),
        ResolvedLocation("Visakhapatnam, India", 17.6868, 83.2185, "Asia/Kolkata", true),
        ResolvedLocation("Thiruvananthapuram, India", 8.5241, 76.9366, "Asia/Kolkata", true),
        ResolvedLocation("Guwahati, India", 26.1445, 91.7362, "Asia/Kolkata", true),
        ResolvedLocation("London, United Kingdom", 51.5074, -0.1278, "Europe/London", false),
        ResolvedLocation("New York, United States", 40.7128, -74.0060, "America/New_York", false),
        ResolvedLocation("Dubai, UAE", 25.2048, 55.2708, "Asia/Dubai", false),
        ResolvedLocation("Singapore", 1.3521, 103.8198, "Asia/Singapore", false)
    )

    fun searchCities(query: String): List<ResolvedLocation> {
        val clean = query.trim().lowercase()
        if (clean.isBlank()) {
            return masterCityDatabase.take(8) // Default suggestions
        }
        // Check historical name mapping first
        val mappedName = historicalMap[clean] ?: clean
        return masterCityDatabase.filter {
            it.cityName.lowercase().contains(clean) || it.cityName.lowercase().contains(mappedName)
        }.sortedByDescending { it.isPreferredIndia }
    }

    fun resolveLocation(query: String): ResolvedLocation {
        val clean = query.trim().lowercase()
        if (clean.isBlank()) {
            return ResolvedLocation("New Delhi, India", 28.6139, 77.2090, "Asia/Kolkata", true)
        }
        // Check historical mapping
        val resolvedQuery = historicalMap[clean] ?: query
        val match = masterCityDatabase.firstOrNull {
            it.cityName.equals(resolvedQuery, ignoreCase = true) || it.cityName.lowercase().contains(clean)
        }
        if (match != null) return match

        // Fallback dynamic location
        return ResolvedLocation(query, 28.6139, 77.2090, "Asia/Kolkata", true)
    }
}
