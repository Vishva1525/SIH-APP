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
import com.pmis.app.screens.AuthScreen
import com.pmis.app.screens.WelcomeScreen
import com.pmis.app.ui.theme.PMISAppTheme

class MainActivitySimple : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        
                        composable("auth") {
                            AuthScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
