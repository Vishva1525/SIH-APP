package com.pmis.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pmis.app.screens.AboutScreen
import com.pmis.app.screens.ContactScreen
import com.pmis.app.screens.EmployersScreen
import com.pmis.app.screens.GuidelinesScreen
import com.pmis.app.screens.HomeScreen
import com.pmis.app.screens.InternScreen
import com.pmis.app.screens.MainScreen
import com.pmis.app.screens.RecommendationScreen
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
                onGetStartedClick = {
                    navController.navigate("main")
                }
            )
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
            HomeScreen()
        }
        
        composable("intern") {
            InternScreen()
        }
        
        composable("recommendation") {
            RecommendationScreen()
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
    }
}
