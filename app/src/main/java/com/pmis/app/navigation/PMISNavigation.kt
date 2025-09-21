package com.pmis.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pmis.app.data.AppState
import com.pmis.app.screens.AboutScreen
import com.pmis.app.screens.AuthScreen
import com.pmis.app.screens.ContactScreen
import com.pmis.app.screens.DashboardScreen
import com.pmis.app.screens.EmployersScreen
import com.pmis.app.screens.EnhancedRecommendationsScreen
import com.pmis.app.screens.GuidelinesScreen
import com.pmis.app.screens.HomeScreen
import com.pmis.app.screens.InternRegistrationScreen
import com.pmis.app.screens.MainScreen
import com.pmis.app.screens.NotificationsScreen
import com.pmis.app.screens.ProjectManagementScreen
import com.pmis.app.screens.RecommendationScreen
import com.pmis.app.screens.RecommendationsScreen
import com.pmis.app.screens.RegisterScreen
import com.pmis.app.screens.StudentsScreen
import com.pmis.app.screens.WelcomeScreen

@Composable
fun PMISNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                navController = navController
            )
        }
        
        composable("auth") {
            AuthScreen(navController = navController)
        }
        
        composable("main") {
            MainScreen(
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        // New hamburger menu screens
        composable("home") {
            HomeScreen(
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        composable("intern") {
            InternRegistrationScreen(navController = navController)
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
    }
}
