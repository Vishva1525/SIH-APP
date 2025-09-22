package com.pmis.app.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pmis.app.data.database.User
import com.pmis.app.screens.InternFormState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticationManager(context: Context) {
    
    private val googleAuthManager = GoogleAuthManager(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Authentication state
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Compose state for immediate UI updates
    var authState by mutableStateOf(AuthState.CHECKING)
        private set
    
    var currentGoogleUser by mutableStateOf<GoogleUser?>(null)
        private set
    
    companion object {
        private const val TAG = "AuthenticationManager"
        private const val PREF_GOOGLE_ID = "google_id"
        private const val PREF_IS_LOGGED_IN = "is_logged_in"
        
        @Volatile
        private var INSTANCE: AuthenticationManager? = null
        
        fun getInstance(context: Context): AuthenticationManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthenticationManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    enum class AuthState {
        CHECKING,
        AUTHENTICATED,
        UNAUTHENTICATED,
        LOADING
    }
    
    init {
        checkAuthenticationState()
    }
    
    /**
     * Check if user is already authenticated
     */
    private fun checkAuthenticationState() {
        authState = AuthState.CHECKING
        _isLoading.value = true
        
        val savedGoogleId = sharedPreferences.getString(PREF_GOOGLE_ID, null)
        val isLoggedIn = sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false)
        
        if (savedGoogleId != null && isLoggedIn) {
            // User was previously authenticated, load their data
            scope.launch {
                loadUserData(savedGoogleId)
            }
        } else {
            authState = AuthState.UNAUTHENTICATED
            _isAuthenticated.value = false
            _isLoading.value = false
        }
    }
    
    /**
     * Load user data from database
     */
    private suspend fun loadUserData(googleId: String) {
        try {
            val user = googleAuthManager.getUserByGoogleId(googleId)
            if (user != null) {
                _currentUser.value = user
                _isAuthenticated.value = true
                authState = AuthState.AUTHENTICATED
                
                // Create GoogleUser for UI
                currentGoogleUser = GoogleUser(
                    googleId = user.googleId,
                    email = user.email,
                    displayName = user.displayName,
                    profilePictureUrl = user.profilePictureUrl
                )
                
                Log.d(TAG, "User data loaded: ${user.email}")
            } else {
                // User not found in database, sign out
                signOut()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load user data", e)
            signOut()
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Sign in with Google
     */
    suspend fun signInWithGoogle(): Result<GoogleUser> {
        authState = AuthState.LOADING
        _isLoading.value = true
        
        return try {
            val result = googleAuthManager.signInWithGoogle()
            
            if (result.isSuccess) {
                val googleUser = result.getOrThrow()
                
                // Save authentication state
                sharedPreferences.edit()
                    .putString(PREF_GOOGLE_ID, googleUser.googleId)
                    .putBoolean(PREF_IS_LOGGED_IN, true)
                    .apply()
                
                // Load user data from database
                scope.launch {
                    loadUserData(googleUser.googleId)
                }
                
                currentGoogleUser = googleUser
                authState = AuthState.AUTHENTICATED
                _isAuthenticated.value = true
                
                Log.d(TAG, "Sign-in successful: ${googleUser.email}")
            } else {
                authState = AuthState.UNAUTHENTICATED
                _isAuthenticated.value = false
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed", e)
            authState = AuthState.UNAUTHENTICATED
            _isAuthenticated.value = false
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Sign out user
     */
    suspend fun signOut() {
        val currentGoogleId = currentGoogleUser?.googleId
        
        // Clear authentication state
        sharedPreferences.edit()
            .remove(PREF_GOOGLE_ID)
            .putBoolean(PREF_IS_LOGGED_IN, false)
            .apply()
        
        // Clear state
        _isAuthenticated.value = false
        _currentUser.value = null
        currentGoogleUser = null
        authState = AuthState.UNAUTHENTICATED
        
        // Sign out from Google Auth Manager
        if (currentGoogleId != null) {
            googleAuthManager.signOut(currentGoogleId)
        }
        
        Log.d(TAG, "User signed out")
    }
    
    /**
     * Save user's form responses
     */
    suspend fun saveFormResponses(formState: InternFormState): Boolean {
        val googleId = currentGoogleUser?.googleId ?: return false
        return googleAuthManager.saveUserFormResponses(googleId, formState)
    }
    
    /**
     * Load user's form responses
     */
    suspend fun loadFormResponses(): InternFormState? {
        val googleId = currentGoogleUser?.googleId ?: return null
        return googleAuthManager.loadUserFormResponses(googleId)
    }
    
    /**
     * Check if user has saved form responses
     */
    suspend fun hasFormResponses(): Boolean {
        return loadFormResponses() != null
    }
}
