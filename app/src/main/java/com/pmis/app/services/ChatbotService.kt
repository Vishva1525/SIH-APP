package com.pmis.app.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object ChatbotService {
    private const val TAG = "ChatbotService"
    private const val WEBHOOK_URL = "https://qiq-ai.app.n8n.cloud/webhook/chatbot"
    private const val TIMEOUT_SECONDS = 30L
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    
    /**
     * Triggers the n8n chatbot workflow
     * @param context Android context
     * @param onComplete Callback when request completes (success or failure)
     */
    suspend fun triggerChatbot(
        context: Context,
        onComplete: () -> Unit = {}
    ) {
        try {
            Log.d(TAG, "Triggering chatbot webhook: $WEBHOOK_URL")
            
            val result = withContext(Dispatchers.IO) {
                makeWebhookRequest(context)
            }
            
            withContext(Dispatchers.Main) {
                when (result) {
                    is ChatbotResult.Success -> {
                        Log.d(TAG, "Chatbot triggered successfully")
                        Toast.makeText(
                            context,
                            "AI Assistant is ready to help!",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Handle the response - could open a chat interface
                        handleChatbotResponse(context, result.response)
                    }
                    is ChatbotResult.Error -> {
                        Log.e(TAG, "Chatbot trigger failed: ${result.message}")
                        Toast.makeText(
                            context,
                            "Unable to connect to AI Assistant. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ChatbotResult.NetworkError -> {
                        Log.e(TAG, "Network error: ${result.message}")
                        Toast.makeText(
                            context,
                            "Network error. Please check your connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                onComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error triggering chatbot", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Something went wrong. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                onComplete()
            }
        }
    }
    
    private suspend fun makeWebhookRequest(context: Context): ChatbotResult {
        return try {
            // Prepare request data
            val requestData = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("source", "pmis_app")
                put("user_agent", "PMIS-Android-App")
                put("action", "open_chatbot")
                put("context", "main_screen")
            }
            
            val requestBody = requestData.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url(WEBHOOK_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "PMIS-Android-App")
                .addHeader("Accept", "application/json")
                .build()
            
            Log.d(TAG, "Sending request: $requestBody")
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Log.d(TAG, "Webhook response: $responseBody")
                
                ChatbotResult.Success(responseBody)
            } else {
                Log.e(TAG, "Webhook failed with code: ${response.code}")
                ChatbotResult.Error("Server returned ${response.code}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error", e)
            ChatbotResult.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            ChatbotResult.Error(e.message ?: "Unknown error")
        }
    }
    
    private fun handleChatbotResponse(context: Context, response: String) {
        try {
            // Parse the response to determine what to do next
            val jsonResponse = JSONObject(response)
            
            // Check if the response contains a URL to open
            if (jsonResponse.has("chat_url")) {
                val chatUrl = jsonResponse.getString("chat_url")
                openChatUrl(context, chatUrl)
            } else if (jsonResponse.has("message")) {
                val message = jsonResponse.getString("message")
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            } else {
                // Default behavior - could open a web view or external app
                openDefaultChatInterface(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing chatbot response", e)
            // Fallback to default behavior
            openDefaultChatInterface(context)
        }
    }
    
    private fun openChatUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening chat URL", e)
            openDefaultChatInterface(context)
        }
    }
    
    private fun openDefaultChatInterface(context: Context) {
        // This could open a web view, external app, or show a dialog
        // For now, we'll just show a toast with instructions
        Toast.makeText(
            context,
            "AI Assistant is ready! Check your browser or the designated chat interface.",
            Toast.LENGTH_LONG
        ).show()
    }
    
    /**
     * Alternative method to trigger chatbot with custom parameters
     */
    suspend fun triggerChatbotWithContext(
        context: Context,
        userContext: String,
        onComplete: () -> Unit = {}
    ) {
        try {
            Log.d(TAG, "Triggering chatbot with context: $userContext")
            
            val result = withContext(Dispatchers.IO) {
                makeContextualWebhookRequest(context, userContext)
            }
            
            withContext(Dispatchers.Main) {
                when (result) {
                    is ChatbotResult.Success -> {
                        Log.d(TAG, "Contextual chatbot triggered successfully")
                        Toast.makeText(
                            context,
                            "AI Assistant is ready with your context!",
                            Toast.LENGTH_SHORT
                        ).show()
                        handleChatbotResponse(context, result.response)
                    }
                    is ChatbotResult.Error -> {
                        Log.e(TAG, "Contextual chatbot trigger failed: ${result.message}")
                        Toast.makeText(
                            context,
                            "Unable to connect to AI Assistant. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ChatbotResult.NetworkError -> {
                        Log.e(TAG, "Network error: ${result.message}")
                        Toast.makeText(
                            context,
                            "Network error. Please check your connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                onComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error triggering contextual chatbot", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Something went wrong. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                onComplete()
            }
        }
    }
    
    private suspend fun makeContextualWebhookRequest(
        context: Context,
        userContext: String
    ): ChatbotResult {
        return try {
            val requestData = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("source", "pmis_app")
                put("user_agent", "PMIS-Android-App")
                put("action", "open_chatbot_with_context")
                put("context", userContext)
                put("screen", "main_screen")
            }
            
            val requestBody = requestData.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url(WEBHOOK_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "PMIS-Android-App")
                .addHeader("Accept", "application/json")
                .build()
            
            Log.d(TAG, "Sending contextual request: $requestData")
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Log.d(TAG, "Contextual webhook response: $responseBody")
                ChatbotResult.Success(responseBody)
            } else {
                Log.e(TAG, "Contextual webhook failed with code: ${response.code}")
                ChatbotResult.Error("Server returned ${response.code}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error", e)
            ChatbotResult.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            ChatbotResult.Error(e.message ?: "Unknown error")
        }
    }
}

sealed class ChatbotResult {
    data class Success(val response: String) : ChatbotResult()
    data class Error(val message: String) : ChatbotResult()
    data class NetworkError(val message: String) : ChatbotResult()
}
