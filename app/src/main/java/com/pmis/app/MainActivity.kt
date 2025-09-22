package com.pmis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pmis.app.auth.AuthenticationManager
import com.pmis.app.navigation.PMISNavigation
import com.pmis.app.ui.theme.PMISAppTheme

class MainActivity : ComponentActivity() {
    
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
                    PMISNavigation(authManager = authManager)
                }
            }
        }
    }
}
