package com.pmis.app.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pmis.app.R
import com.pmis.app.auth.AuthenticationManager
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    authManager: AuthenticationManager
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Observe authentication state
    val authState by remember { derivedStateOf { authManager.authState } }
    val isLoading by authManager.isLoading.collectAsState()
    
    // Handle authentication state changes
    LaunchedEffect(authState) {
        when (authState) {
            AuthenticationManager.AuthState.AUTHENTICATED -> {
                // Check if user has form responses
                val hasFormData = authManager.hasFormResponses()
                if (hasFormData) {
                    // User has previous data, go to recommendations
                    navController.navigate("ml_recommendations") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // New user, go to form
                    navController.navigate("intern_registration") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            else -> { /* Stay on login screen */ }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo/Icon (if available)
            // You can replace this with your app logo
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleStart)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PMIS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = WhiteColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Welcome Text
            Text(
                text = "Welcome to PMIS",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Find your perfect internship with AI-powered recommendations",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Google Sign-In Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = authManager.signInWithGoogle()
                        if (result.isFailure) {
                            Toast.makeText(
                                context,
                                "Sign-in failed: ${result.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhiteColor,
                    contentColor = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PurpleStart,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google Icon (you can replace with actual Google logo)
                        Text(
                            text = "🔍",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Privacy and Terms Text
            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            // Loading state overlay
            if (authState == AuthenticationManager.AuthState.CHECKING) {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PurpleStart,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Checking authentication...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PMISAppTheme {
        // Note: This preview won't work properly without a real AuthenticationManager
        // LoginScreen(navController = rememberNavController(), authManager = ...)
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login Screen Preview\n(Requires AuthenticationManager)",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

