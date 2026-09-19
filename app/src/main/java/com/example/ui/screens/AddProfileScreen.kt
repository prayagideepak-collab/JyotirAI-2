package com.example.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.JyotishDatabase
import com.example.model.ProfileEntity
import com.example.ui.components.CitySearchView
import com.example.ui.components.MapPinDropDialog
import com.example.ui.theme.SaffronPrimary
import com.example.viewmodel.CitySearchViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    onBack: () -> Unit,
    citySearchViewModel: CitySearchViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val dao = remember { JyotishDatabase.getDatabase(context).profileDao() }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var onboardingStep by remember { mutableStateOf(0) } // 0 = Form Input, 1 = Final Review Screen

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("पुरुष") } // पुरुष / महिला / अन्य
    var birthDate by remember { mutableStateOf("15/08/1995") }
    var birthTime by remember { mutableStateOf("10:30 AM") }
    var birthPlaceQuery by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("25.4358") }
    var longitude by remember { mutableStateOf("81.8463") }
    var timezone by remember { mutableStateOf("Asia/Kolkata") }
    var capturedImageUri by remember { mutableStateOf<String?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }

    val searchQuery by citySearchViewModel.searchQuery.collectAsState()
    val searchResults by citySearchViewModel.searchResults.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val file = File(context.filesDir, "new_profile_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    capturedImageUri = Uri.fromFile(file).toString()
                } catch (e: Exception) {
                    // handle error
                }
            }
        }
    }

    fun showNativeDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCal = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            if (selectedCal.after(Calendar.getInstance())) {
                Toast.makeText(context, "जन्म तिथि भविष्य की नहीं हो सकती।", Toast.LENGTH_SHORT).show()
                return@DatePickerDialog
            }
            val formatted = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            birthDate = formatted
        }, year, month, day).show()
    }

    fun showNativeTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val hourFormatted = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            birthTime = String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm)
        }, hour, minute, false).show()
    }

    @SuppressLint("MissingPermission")
    fun fetchGpsLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    latitude = lat.toString()
                    longitude = lon.toString()

                    scope.launch(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale("hi", "IN"))
                            val addresses = geocoder.getFromLocation(lat, lon, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Prayagraj"
                                val state = address.adminArea ?: "Uttar Pradesh"
                                val resolvedName = "$city, $state, India"
                                withContext(Dispatchers.Main) {
                                    birthPlaceQuery = resolvedName
                                    citySearchViewModel.onQueryChanged(city)
                                    Toast.makeText(context, "GPS स्थान प्राप्त हुआ: $resolvedName", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "स्थान प्राप्त हुआ (Lat: $lat, Lon: $lon)", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "GPS कोऑर्डिनेट्स सेट किए गए!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "GPS लोकेशन उपलब्ध नहीं है। कृपया मैन्युअल रूप से खोजें।", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "GPS त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (onboardingStep == 0) "नया जन्म प्रोफाइल जोड़ें (Onboarding)" else "विवरण समीक्षा (Final Review)") },
                navigationIcon = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        if (onboardingStep == 1) {
                            onboardingStep = 0
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (onboardingStep == 0) {
                item {
                    // Camera Avatar Picker
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                cameraLauncher.launch(null)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!capturedImageUri.isNullOrBlank()) {
                            AsyncImage(model = capturedImageUri, contentDescription = "Profile Photo", modifier = Modifier.fillMaxSize())
                        } else {
                            Icon(Icons.Default.Person, contentDescription = "User", tint = SaffronPrimary, modifier = Modifier.size(36.dp))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "प्रोफाइल फोटो के लिए आइकॉन पर टैप करें (अधिकतम 3 प्रोफाइल)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("पूरा नाम (Full Name) *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Gender Selection
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("लिंग (Gender):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("पुरुष", "महिला", "अन्य").forEach { option ->
                                OutlinedButton(
                                    onClick = { gender = option },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (gender == option) SaffronPrimary.copy(alpha = 0.15f) else Color.Transparent,
                                        contentColor = if (gender == option) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(option, fontSize = 13.sp, fontWeight = if (gender == option) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }

                // Date of Birth Picker (Disabled text input / readOnly = true)
                item {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("जन्म तिथि (Date of Birth) *") },
                        trailingIcon = {
                            IconButton(onClick = { showNativeDatePicker() }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = SaffronPrimary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showNativeDatePicker() },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Time of Birth Picker (Disabled text input / readOnly = true)
                item {
                    OutlinedTextField(
                        value = birthTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("जन्म समय (Time of Birth) *") },
                        trailingIcon = {
                            IconButton(onClick = { showNativeTimePicker() }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Pick Time", tint = SaffronPrimary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showNativeTimePicker() },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Place of Birth, GPS, Map Pin Drop & City Search
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("जन्म स्थान और कोऑर्डिनेट्स (Birth Location)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            fetchGpsLocation()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.GpsFixed, contentDescription = "GPS", tint = SaffronPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("GPS", fontSize = 10.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            showMapDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.LocationOn, contentDescription = "Map", tint = SaffronPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("नक्शा पिन", fontSize = 10.sp)
                                    }
                                }
                            }

                            CitySearchView(
                                query = birthPlaceQuery.ifBlank { searchQuery },
                                onQueryChanged = { query ->
                                    birthPlaceQuery = query
                                    citySearchViewModel.onQueryChanged(query)
                                },
                                suggestions = searchResults,
                                onCitySelected = { city ->
                                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                    birthPlaceQuery = "${city.cityName}, ${city.state}, India"
                                    latitude = city.latitude.toString()
                                    longitude = city.longitude.toString()
                                    timezone = city.timezone
                                    citySearchViewModel.onQueryChanged("")
                                }
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = latitude,
                                    onValueChange = { latitude = it },
                                    label = { Text("अक्षांश (Lat)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = longitude,
                                    onValueChange = { longitude = it },
                                    label = { Text("देशांतर (Lon)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }

                            Text("Timezone: $timezone | Historical names mapped (Allahabad -> Prayagraj)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            if (name.isBlank()) {
                                Toast.makeText(context, "कृपया पूरा नाम दर्ज करें", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (birthPlaceQuery.isBlank()) {
                                Toast.makeText(context, "कृपया जन्म स्थान चुनें या खोजें", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            // Form validation passed -> proceed to Final Review screen
                            onboardingStep = 1
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Text("समीक्षा करें (Review Birth Data)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            } else {
                // Step 1: Final Review Screen in Hindi
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("📋 प्रोफाइल जन्म विवरण समीक्षा (Final Review)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                            ReviewRow("पूरा नाम (Name):", name)
                            ReviewRow("लिंग (Gender):", gender)
                            ReviewRow("जन्म तिथि (DOB):", birthDate)
                            ReviewRow("जन्म समय (Time):", birthTime)
                            ReviewRow("जन्म स्थान (Place):", birthPlaceQuery)
                            ReviewRow("अक्षांश / देशांतर:", "$latitude, $longitude")
                            ReviewRow("टाइमज़ोन (Timezone):", timezone)

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "यह विवरण आपकी जन्म कुंडली, विंशोत्तरी महादशा, पंचांग और दैनिक भविष्यवाणियों के लिए उपयोग किया जाएगा।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onboardingStep = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("संशोधित करें (Edit)")
                        }

                        Button(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                scope.launch(Dispatchers.IO) {
                                    val count = dao.getProfileCount()
                                    if (count >= 3) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "अधिकतम 3 प्रोफाइल समर्थित हैं।", Toast.LENGTH_SHORT).show()
                                        }
                                        return@launch
                                    }
                                    val finalPlace = birthPlaceQuery

                                    val newProfile = ProfileEntity(
                                        name = name.trim(),
                                        birthDate = birthDate,
                                        birthTime = birthTime,
                                        birthPlace = finalPlace,
                                        latitude = latitude.toDoubleOrNull() ?: 25.4358,
                                        longitude = longitude.toDoubleOrNull() ?: 81.8463,
                                        timezone = timezone,
                                        gender = gender,
                                        isDefault = count == 0,
                                        profileImageUri = capturedImageUri,
                                        isExample = false,
                                        isDemo = false
                                    )
                                    dao.insertProfile(newProfile)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "प्रोफाइल सफलतापूर्वक जोड़ी गई एवं गणना प्रारंभ हुई!", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("पुष्टि करें (Confirm & Save)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    if (showMapDialog) {
        MapPinDropDialog(
            initialLat = latitude.toDoubleOrNull() ?: 25.4358,
            initialLon = longitude.toDoubleOrNull() ?: 81.8463,
            onLocationConfirmed = { lat, lon, placeName ->
                latitude = lat.toString()
                longitude = lon.toString()
                birthPlaceQuery = placeName
                showMapDialog = false
                Toast.makeText(context, "मानचित्र स्थान सेट किया गया!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showMapDialog = false }
        )
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}
