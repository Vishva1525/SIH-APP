package com.pmis.app.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pmis.app.ui.theme.PMISAppTheme
import kotlinx.coroutines.launch

// Custom colors for auth buttons
private val EmailBlue = Color(0xFF4285F4)
private val PhoneGreen = Color(0xFF34A853)
private val GoogleWhite = Color(0xFFFFFFFF)
private val GoogleBlack = Color(0xFF000000)

// Custom colors for input field borders
private val ActiveGreen = Color(0xFF4CAF50)
private val InactiveGray = Color(0xFFCCCCCC)
private val ErrorRed = Color(0xFFF44336)

// Helper functions for input validation
private fun isValidEmail(input: String): Boolean {
    if (!input.contains("@")) return false
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(input)
}

private fun isValidPhoneNumber(input: String): Boolean {
    val digitsOnly = input.replace(Regex("[^0-9]"), "")
    return digitsOnly.length >= 10 && digitsOnly.all { it.isDigit() }
}

private fun getInputType(input: String): String {
    return when {
        input.contains("@") -> "email"
        input.replace(Regex("[^0-9]"), "").length >= 10 -> "phone"
        else -> "unknown"
    }
}

private fun isInputValid(input: String): Boolean {
    return when (getInputType(input)) {
        "email" -> isValidEmail(input)
        "phone" -> isValidPhoneNumber(input)
        else -> false
    }
}

// TODO: Replace with Firebase Auth integration later
private fun handleGoogleSignIn(context: android.content.Context, navController: androidx.navigation.NavController) {
    Toast.makeText(context, "Google Sign-in Clicked", Toast.LENGTH_SHORT).show()
    Log.d("AuthScreen", "Google Sign-in button clicked")
    
    // Navigate to main screen after Google sign-in
    // TODO: Replace with actual Google Auth logic
    navController.navigate("main")
}

@Composable
fun AuthScreen(navController: NavController) {
    // Debug log to verify AuthScreen is loaded
    Log.d("AuthScreen", "AuthScreen composable loaded - NEW VERSION")
    
    // State management
    var showUnifiedInput by remember { mutableStateOf(false) }
    var unifiedInputText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Login or Sign Up (NEW)",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Subtitle
        Text(
            text = "Choose a method to continue",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Continue with Email button
        Button(
            onClick = { 
                showUnifiedInput = !showUnifiedInput
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmailBlue
            )
        ) {
            Text(
                text = "Continue with Email",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Continue with Phone Number button
        Button(
            onClick = { 
                showUnifiedInput = !showUnifiedInput
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PhoneGreen
            )
        ) {
            Text(
                text = "Continue with Phone Number",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Continue with Google button
        Button(
            onClick = { handleGoogleSignIn(context, navController) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoogleWhite
            )
        ) {
            Text(
                text = "🔍 Continue with Google",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = GoogleBlack
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Unified Email/Phone input section
        if (showUnifiedInput) {
            // Animated colors for smooth transitions
            val animatedBorderColor by animateColorAsState(
                targetValue = when {
                    showError -> ErrorRed
                    isFocused -> ActiveGreen
                    else -> InactiveGray
                },
                animationSpec = tween(durationMillis = 300),
                label = "borderColor"
            )
            
            val animatedLabelColor by animateColorAsState(
                targetValue = when {
                    showError -> ErrorRed
                    isFocused -> ActiveGreen
                    else -> InactiveGray
                },
                animationSpec = tween(durationMillis = 300),
                label = "labelColor"
            )
            
            val animatedElevation by animateDpAsState(
                targetValue = if (isFocused) 4.dp else 0.dp,
                animationSpec = tween(durationMillis = 300),
                label = "elevation"
            )
            
            OutlinedTextField(
                value = unifiedInputText,
                onValueChange = { 
                    unifiedInputText = it
                    // Clear error when user starts typing
                    if (showError) {
                        showError = false
                        errorMessage = ""
                    }
                },
                label = { Text("Email or Phone") },
                placeholder = { Text("Enter your email or phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = showError,
                supportingText = if (showError) {
                    { Text(errorMessage, color = ErrorRed) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = animatedBorderColor,
                    unfocusedBorderColor = animatedBorderColor,
                    errorBorderColor = animatedBorderColor,
                    focusedLabelColor = animatedLabelColor,
                    unfocusedLabelColor = animatedLabelColor,
                    errorLabelColor = animatedLabelColor,
                    cursorColor = ActiveGreen
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .shadow(
                        elevation = animatedElevation,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = ActiveGreen.copy(alpha = 0.2f),
                        spotColor = ActiveGreen.copy(alpha = 0.2f)
                    )
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    val inputType = getInputType(unifiedInputText)
                    val isValid = isInputValid(unifiedInputText)
                    
                    if (isValid) {
                        // Clear any previous errors
                        showError = false
                        errorMessage = ""
                        
                        val displayType = when (inputType) {
                            "email" -> "Email"
                            "phone" -> "Phone Number"
                            else -> "Input"
                        }
                        
                        // Log the input and show snackbar
                        Log.d("AuthScreen", "$displayType entered: $unifiedInputText")
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("$displayType entered: $unifiedInputText")
                        }
                        
                        // Navigate to main screen after successful authentication
                        // TODO: Replace with actual authentication logic
                        navController.navigate("main")
                    } else {
                        // Show validation error
                        showError = true
                        errorMessage = "Enter a valid email or phone number"
                        Log.d("AuthScreen", "Invalid input: $unifiedInputText")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = unifiedInputText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (getInputType(unifiedInputText)) {
                        "email" -> EmailBlue
                        "phone" -> PhoneGreen
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                val buttonText = when (getInputType(unifiedInputText)) {
                    "email" -> "Submit Email"
                    "phone" -> "Submit Phone"
                    else -> "Submit"
                }
                
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
        }
        
        // Snackbar host for showing messages
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    PMISAppTheme {
        AuthScreen(navController = rememberNavController())
    }
}