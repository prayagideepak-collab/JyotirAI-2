package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
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
import coil.compose.AsyncImage
import com.example.data.JyotishDatabase
import com.example.model.Profile
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val dao = remember { JyotishDatabase.getDatabase(context).profileDao() }

    var profiles by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<Profile?>(null) }
    var profileForCamera by remember { mutableStateOf<Profile?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null && profileForCamera != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val file = File(context.filesDir, "profile_${profileForCamera!!.id}_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    val uriStr = Uri.fromFile(file).toString()
                    val updated = profileForCamera!!.copy(profileImageUri = uriStr)
                    dao.insertProfile(updated)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "प्रोफाइल फोटो सहेजी गई!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "फोटो सहेजने में विफल", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Form state for add dialog
    var newName by remember { mutableStateOf("") }
    var newDob by remember { mutableStateOf("15/08/1995") }
    var newTime by remember { mutableStateOf("10:30 AM") }
    var newPlace by remember { mutableStateOf("New Delhi") }
    var newLat by remember { mutableStateOf("28.6139") }
    var newLon by remember { mutableStateOf("77.2090") }

    LaunchedEffect(Unit) {
        dao.getAllProfiles().collect { list ->
            profiles = list
            if (list.isEmpty()) {
                dao.insertProfile(
                    Profile(
                        name = "Example",
                        birthDate = "01/01/2000",
                        birthTime = "12:00 PM",
                        birthPlace = "Prayagraj",
                        latitude = 25.4358,
                        longitude = 81.8463,
                        isDefault = false
                    )
                )
                dao.insertProfile(
                    Profile(
                        name = "JyotirAI",
                        birthDate = "03/09/2026",
                        birthTime = "05:10 PM",
                        birthPlace = "Prayagraj, Uttar Pradesh, India",
                        latitude = 25.4358,
                        longitude = 81.8463,
                        isDefault = true
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("जन्म प्रोफाइल प्रबंधन (Max 3 Profiles)") },
                navigationIcon = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        if (profiles.size >= 3) {
                            Toast.makeText(context, "अधिकतम 3 प्रोफाइल ही बनाए जा सकते हैं (Maximum 3 profiles allowed).", Toast.LENGTH_SHORT).show()
                        } else {
                            showAddDialog = true
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Profile")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "प्रत्येक प्रोफाइल की अपनी विशिष्ट फोटो, जन्म तिथि, समय और स्थान के आधार पर सटीक गणना की जाती है।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(profiles) { profile ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar with Camera capture button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                    profileForCamera = profile
                                    cameraLauncher.launch(null)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profile.profileImageUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = profile.profileImageUri,
                                    contentDescription = profile.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = "User", tint = SaffronPrimary, modifier = Modifier.size(28.dp))
                            }
                            // Camera overlay icon
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Capture", tint = Color.White, modifier = Modifier.size(16.dp).padding(bottom = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                if (profile.isDefault) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SaffronPrimary
                                    ) {
                                        Text("Active", fontSize = 9.sp, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("DOB: ${profile.birthDate} | ${profile.birthTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Place: ${profile.birthPlace}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Actions
                        Column(horizontalAlignment = Alignment.End) {
                            if (!profile.isDefault) {
                                TextButton(onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    scope.launch(Dispatchers.IO) {
                                        dao.clearDefaultFlags()
                                        dao.setDefaultProfile(profile.id)
                                    }
                                }) {
                                    Text("चुनें (Select)", fontSize = 12.sp)
                                }
                            }
                            if (profiles.size > 1) {
                                IconButton(onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                    showDeleteConfirm = profile
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Profile Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("नया जन्म प्रोफाइल जोड़ें (${profiles.size}/3)") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("पूरा नाम (Full Name)") }, singleLine = true)
                        OutlinedTextField(value = newDob, onValueChange = { newDob = it }, label = { Text("जन्म तिथि (DD/MM/YYYY)") }, singleLine = true)
                        OutlinedTextField(value = newTime, onValueChange = { newTime = it }, label = { Text("जन्म समय (HH:MM AM/PM)") }, singleLine = true)
                        OutlinedTextField(value = newPlace, onValueChange = { newPlace = it }, label = { Text("जन्म स्थान (City/Place)") }, singleLine = true)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = newLat, onValueChange = { newLat = it }, label = { Text("अक्षांश (Lat)") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = newLon, onValueChange = { newLon = it }, label = { Text("देशांतर (Lon)") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        if (newName.isBlank()) {
                            Toast.makeText(context, "कृपया नाम दर्ज करें (Enter name)", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        if (profiles.size >= 3) {
                            Toast.makeText(context, "अधिकतम 3 प्रोफाइल की सीमा पूरी हो चुकी है।", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                            return@TextButton
                        }
                        scope.launch(Dispatchers.IO) {
                            val lat = newLat.toDoubleOrNull() ?: 25.4358
                            val lon = newLon.toDoubleOrNull() ?: 81.8463
                            dao.insertProfile(
                                Profile(
                                    name = newName,
                                    birthDate = newDob,
                                    birthTime = newTime,
                                    birthPlace = newPlace,
                                    latitude = lat,
                                    longitude = lon,
                                    isDefault = profiles.isEmpty()
                                )
                            )
                            withContext(Dispatchers.Main) {
                                newName = ""
                                showAddDialog = false
                            }
                        }
                    }) {
                        Text("जोड़ें (Add)")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("रद्द करें (Cancel)")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        showDeleteConfirm?.let { profileToDelete ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("प्रोफाइल हटाएं (Delete Profile)") },
                text = { Text("क्या आप '${profileToDelete.name}' को हटाना चाहते हैं?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch(Dispatchers.IO) {
                            dao.deleteProfile(profileToDelete)
                            withContext(Dispatchers.Main) {
                                showDeleteConfirm = null
                            }
                        }
                    }) {
                        Text("हटाएं (Delete)", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) {
                        Text("रद्द करें (Cancel)")
                    }
                }
            )
        }
    }
}
