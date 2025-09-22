package com.pmis.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pmis.app.auth.AuthenticationManager
import com.pmis.app.data.AppState
import com.pmis.app.screens.AboutScreen
import com.pmis.app.screens.ActivityFeedScreen
import com.pmis.app.screens.ApplicationDetailsScreen
import com.pmis.app.screens.ApplicationsScreen
import com.pmis.app.screens.ContactScreen
import com.pmis.app.screens.DashboardScreen
import com.pmis.app.screens.EmployersScreen
import com.pmis.app.screens.EnhancedRecommendationsScreen
import com.pmis.app.screens.GuidelinesScreen
import com.pmis.app.screens.HomeScreen
import com.pmis.app.screens.InternRegistrationScreen
import com.pmis.app.screens.InternStep
import com.pmis.app.screens.InternshipDetailsScreen
import com.pmis.app.screens.InternshipsSearchScreen
import com.pmis.app.screens.LoginScreen
import com.pmis.app.screens.MainScreen
import com.pmis.app.screens.NotificationsScreen
import com.pmis.app.screens.ProfileEditScreen
import com.pmis.app.screens.ProfileHistoryScreen
import com.pmis.app.screens.ProjectManagementScreen
import com.pmis.app.screens.RecommendationDetailsScreen
import com.pmis.app.screens.RecommendationScreen
import com.pmis.app.screens.RecommendationsScreen
import com.pmis.app.screens.RegisterScreen
import com.pmis.app.screens.StudentsScreen
import com.pmis.app.screens.WelcomeScreen

@Composable
fun PMISNavigation(authManager: AuthenticationManager) {
    val navController = rememberNavController()
    val isAuthenticated by authManager.isAuthenticated.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "main" else "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                navController = navController
            )
        }
        
        composable("login") {
            LoginScreen(
                navController = navController,
                authManager = authManager
            )
        }
        
        composable("main") {
            MainScreen(
                onNavigateToScreen = { route ->
                    val mapped = when (route) {
                        "intern" -> "intern_registration"
                        else -> route
                    }
                    navController.navigate(mapped)
                }
            )
        }
        
        // New hamburger menu screens
        composable("home") {
            HomeScreen(
                onNavigateToScreen = { route ->
                    val mapped = when (route) {
                        "intern" -> "intern_registration"
                        else -> route
                    }
                    navController.navigate(mapped)
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToScreen = { route ->
                    try {
                        val mapped = when (route) {
                            "intern" -> "intern_registration"
                            else -> route
                        }
                        navController.navigate(mapped)
                    } catch (e: Exception) {
                        // Log navigation error but don't crash
                        android.util.Log.e("PMISNavigation", "Navigation error for route: $route", e)
                    }
                },
                onBackClick = {
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        android.util.Log.e("PMISNavigation", "Back navigation error", e)
                    }
                }
            )
        }
        
        composable("intern_registration") {
            InternRegistrationScreen(
                navController = navController,
                authManager = authManager,
                startStep = InternStep.Resume
            )
        }
        
        composable("recommendation") {
            RecommendationScreen()
        }
        
        composable("ml_recommendations") {
            EnhancedRecommendationsScreen(
                internFormState = AppState.internFormData,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Original navigation drawer screens (kept for compatibility)
        composable("about") {
            AboutScreen()
        }
        
        composable("register") {
            RegisterScreen()
        }
        
        composable("students") {
            StudentsScreen()
        }
        
        composable("employers") {
            EmployersScreen()
        }
        
        composable("guidelines") {
            GuidelinesScreen()
        }
        
        composable("contact") {
            ContactScreen()
        }
        
        composable("notifications") {
            NotificationsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("project_management") {
            ProjectManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Dashboard navigation routes
        composable("applications") {
            ApplicationsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("internships_search") {
            InternshipsSearchScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("profile_edit") {
            ProfileEditScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("activity_feed") {
            ActivityFeedScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("recommendation_details") {
            RecommendationDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("application_details") {
            ApplicationDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("profile_history") {
            ProfileHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("internship_details") {
            InternshipDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
