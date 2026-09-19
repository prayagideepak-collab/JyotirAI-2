package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JyotishDatabase
import com.example.model.ProfileEntity
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementScreen(
    onNavigateToAdd: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { JyotishDatabase.getDatabase(context).profileDao() }

    var profiles by remember { mutableStateOf<List<ProfileEntity>>(emptyList()) }
    var showDeleteConfirm by remember { mutableStateOf<ProfileEntity?>(null) }

    LaunchedEffect(Unit) {
        dao.getAllProfiles().collect { list ->
            profiles = list
            if (list.isEmpty()) {
                dao.insertProfile(
                    ProfileEntity(
                        name = "Example",
                        birthDate = "01/01/2000",
                        birthTime = "12:00 PM",
                        birthPlace = "Prayagraj",
                        latitude = 25.4358,
                        longitude = 81.8463,
                        isDefault = false,
                        isExample = true,
                        isDemo = false
                    )
                )
                dao.insertProfile(
                    ProfileEntity(
                        name = "JyotirAI",
                        birthDate = "03/09/2026",
                        birthTime = "05:10 PM",
                        birthPlace = "Prayagraj, Uttar Pradesh, India",
                        latitude = 25.4358,
                        longitude = 81.8463,
                        isDefault = true,
                        isExample = false,
                        isDemo = true
                    )
                )
            }
        }
    }

    val profileCount = profiles.size
    val isLimitReached = profileCount >= 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("प्रोफाइल प्रबंधन (Profiles: $profileCount/3)") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = {
                        if (isLimitReached) {
                            Toast.makeText(context, "अधिकतम 3 प्रोफाइल की सीमा पूरी हो चुकी है (3/3 Reached).", Toast.LENGTH_SHORT).show()
                        } else {
                            onNavigateToAdd()
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
            // Count warning banner if reaching limit
            if (isLimitReached) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "⚠️ 3/3 Profiles Reached. Maximum profile limit is reached. Delete a profile to add a new one.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "सक्रिय प्रोफाइल से ही आपकी कुंडली, राशिफल और पंचांग गणना की जाती है।",
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = "User", tint = SaffronPrimary)
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
                            Text("Place: ${profile.birthPlace} (Lat: ${profile.latitude}, Lon: ${profile.longitude})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (!profile.isDefault) {
                                TextButton(onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        dao.clearDefaultFlags()
                                        dao.setDefaultProfile(profile.id)
                                    }
                                }) {
                                    Text("सक्रिय करें (Select)", fontSize = 12.sp)
                                }
                            }
                            if (profiles.size > 1) {
                                IconButton(onClick = { showDeleteConfirm = profile }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (isLimitReached) {
                            Toast.makeText(context, "अधिकतम 3 प्रोफाइल की सीमा पूरी हो चुकी है।", Toast.LENGTH_SHORT).show()
                        } else {
                            onNavigateToAdd()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("नया प्रोफाइल जोड़ें (Add Profile)", fontSize = 14.sp)
                }
            }
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
                                Toast.makeText(context, "प्रोफाइल हटा दिया गया", Toast.LENGTH_SHORT).show()
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
