package com.pmis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pmis.app.navigation.PMISNavigation
import com.pmis.app.ui.theme.PMISAppTheme

/**
 * Alternative MainActivity that includes full navigation
 * To use this instead of the current MainActivity:
 * 1. Rename this file to MainActivity.kt
 * 2. Rename the current MainActivity.kt to something else
 * 3. Update the AndroidManifest.xml if needed
 */
class MainActivityWithNavigation : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PMISAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PMISNavigation()
                }
            }
        }
    }
}
