package com.pmis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pmis.app.auth.AuthenticationManager
import com.pmis.app.screens.LoginScreen
import com.pmis.app.screens.WelcomeScreen
import com.pmis.app.ui.theme.PMISAppTheme

class MainActivitySimple : ComponentActivity() {
    
    private lateinit var authManager: AuthenticationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AuthenticationManager
        authManager = AuthenticationManager.getInstance(this)
        
        setContent {
            PMISAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation setup with Jetpack Compose
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
                        
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                authManager = authManager
                            )
                        }
                    }
                }
            }
        }
    }
}
