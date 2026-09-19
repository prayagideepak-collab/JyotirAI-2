package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(celestialTheme = com.example.ui.theme.ThemeManager.isCelestial) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { it / 4 } },
                        exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { -it / 4 } },
                        popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { -it / 4 } },
                        popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { it / 4 } }
                    ) {
                        composable("home") {
                            HomeScreen(onNavigate = { route -> navController.navigate(route) })
                        }
                        composable("ai_chat") {
                            AiChatScreen(onBack = { navController.popBackStack() })
                        }
                        composable("kundli") {
                            KundliScreen(onBack = { navController.popBackStack() })
                        }
                        composable("panchang") {
                            PanchangScreen(onBack = { navController.popBackStack() })
                        }
                        composable("dasha") {
                            DashaScreen(onBack = { navController.popBackStack() })
                        }
                        composable("yogas") {
                            YogasScreen(onBack = { navController.popBackStack() })
                        }
                        composable("muhurta") {
                            MuhurtaScreen(onBack = { navController.popBackStack() })
                        }
                        composable("compatibility") {
                            CompatibilityScreen(onBack = { navController.popBackStack() })
                        }
                        composable("rashifal") {
                            RashifalScreen(onBack = { navController.popBackStack() })
                        }
                        composable("daily_horoscope") {
                            DailyHoroscopeScreen(onBack = { navController.popBackStack() })
                        }
                        composable("numerology") {
                            NumerologyScreen(onBack = { navController.popBackStack() })
                        }
                        composable("readings") {
                            ReadingsScreen(onBack = { navController.popBackStack() })
                        }
                        composable("profiles") {
                            ProfileManagementScreen(
                                onNavigateToAdd = { navController.navigate("add_profile") },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("add_profile") {
                            AddProfileScreen(onBack = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
