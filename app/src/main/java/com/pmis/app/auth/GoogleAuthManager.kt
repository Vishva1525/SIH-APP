package com.pmis.app.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.pmis.app.R
import com.pmis.app.data.database.AppDatabase
import com.pmis.app.data.database.User
import com.pmis.app.screens.InternFormState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GoogleUser(
    val googleId: String,
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String?
)

class GoogleAuthManager(private val context: Context) {
    
    private val credentialManager = CredentialManager.create(context)
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    
    companion object {
        private const val TAG = "GoogleAuthManager"
    }
    
    /**
     * Sign in with Google using the new Credential Manager API
     */
    suspend fun signInWithGoogle(): Result<GoogleUser> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting Google Sign-In process")
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_oauth_client_id))
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            
            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val idToken = googleIdCredential.idToken
                if (idToken.isNullOrBlank()) {
                    Log.e(TAG, "Missing Google ID token in credential")
                    return@withContext Result.failure(Exception("Missing Google ID token"))
                }

                val payload = parseJwtPayload(idToken)
                val sub = payload["sub"] as? String ?: ""
                val email = payload["email"] as? String ?: ""

                val googleUser = GoogleUser(
                    googleId = sub,
                    email = email,
                    displayName = googleIdCredential.displayName,
                    profilePictureUrl = googleIdCredential.profilePictureUri?.toString()
                )

                Log.d(TAG, "Google Sign-In successful for: ${googleUser.email}")

                // Save or update user in database
                saveUserToDatabase(googleUser)

                Result.success(googleUser)
            } else {
                Log.e(TAG, "Unexpected credential type: ${credential.type}")
                Result.failure(Exception("Unexpected credential type"))
            }
            
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google Sign-In failed", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In", e)
            Result.failure(e)
        }
    }

    private fun parseJwtPayload(idToken: String): Map<String, Any> {
        return try {
            val parts = idToken.split(".")
            if (parts.size < 2) return emptyMap()
            val payloadB64 = parts[1]
            val decoded = java.util.Base64.getUrlDecoder().decode(payloadB64)
            val json = String(decoded, Charsets.UTF_8)
            val gson = com.google.gson.Gson()
            @Suppress("UNCHECKED_CAST")
            gson.fromJson(json, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ID token payload", e)
            emptyMap()
        }
    }
    
    /**
     * Save or update user in the local database
     */
    private suspend fun saveUserToDatabase(googleUser: GoogleUser) {
        try {
            val existingUser = userDao.getUserByGoogleId(googleUser.googleId)
            
            if (existingUser != null) {
                // User exists, update last login time
                userDao.updateLastLogin(googleUser.googleId, System.currentTimeMillis())
                Log.d(TAG, "Updated existing user: ${googleUser.email}")
            } else {
                // New user, create record
                val newUser = User(
                    googleId = googleUser.googleId,
                    email = googleUser.email,
                    displayName = googleUser.displayName,
                    profilePictureUrl = googleUser.profilePictureUrl,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis(),
                    formResponses = null
                )
                
                userDao.insertOrUpdateUser(newUser)
                Log.d(TAG, "Created new user: ${googleUser.email}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user to database", e)
        }
    }
    
    /**
     * Get user by Google ID
     */
    suspend fun getUserByGoogleId(googleId: String): User? = withContext(Dispatchers.IO) {
        try {
            userDao.getUserByGoogleId(googleId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user from database", e)
            null
        }
    }
    
    /**
     * Save user's form responses
     */
    suspend fun saveUserFormResponses(googleId: String, formState: InternFormState): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByGoogleId(googleId)
            if (user != null) {
                val updatedUser = user.copy(
                    formResponses = com.google.gson.Gson().toJson(formState)
                )
                userDao.insertOrUpdateUser(updatedUser)
                Log.d(TAG, "Saved form responses for user: ${user.email}")
                true
            } else {
                Log.e(TAG, "User not found when trying to save form responses")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save form responses", e)
            false
        }
    }
    
    /**
     * Load user's form responses
     */
    suspend fun loadUserFormResponses(googleId: String): InternFormState? = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByGoogleId(googleId)
            if (user?.formResponses != null) {
                val gson = com.google.gson.Gson()
                gson.fromJson(user.formResponses, InternFormState::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load form responses", e)
            null
        }
    }
    
    /**
     * Get the last logged in user (for auto-login)
     */
    suspend fun getLastLoggedInUser(): User? = withContext(Dispatchers.IO) {
        try {
            userDao.getLastLoggedInUser()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get last logged in user", e)
            null
        }
    }
    
    /**
     * Sign out user (clear local session data)
     */
    suspend fun signOut(googleId: String) = withContext(Dispatchers.IO) {
        try {
            // Note: We don't delete the user record, just clear any session data
            Log.d(TAG, "User signed out: $googleId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sign out user", e)
        }
    }
}
