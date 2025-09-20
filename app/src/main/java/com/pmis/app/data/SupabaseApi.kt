package com.pmis.app.data

import android.content.Context
import android.util.Log
import com.pmis.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object SupabaseApi {
    private const val TAG = "SupabaseApi"
    
    fun getSupabaseUrl(context: Context): String {
        return context.getString(R.string.supabase_url)
    }
    
    fun getSupabaseAnonKey(context: Context): String {
        return context.getString(R.string.supabase_anon_key)
    }
    
    suspend fun signInWithGoogle(
        context: Context,
        idToken: String
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val url = "${getSupabaseUrl(context)}/auth/v1/token?grant_type=id_token"
            val client = OkHttpClient()
            
            val requestBody = JSONObject().apply {
                put("provider", "google")
                put("id_token", idToken)
            }.toString()
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .addHeader("apikey", getSupabaseAnonKey(context))
                .addHeader("Authorization", "Bearer ${getSupabaseAnonKey(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            
            val response: Response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "{}")
                Log.d(TAG, "Supabase sign-in successful: $responseBody")
                Result.success(jsonResponse)
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e(TAG, "Supabase sign-in failed: ${response.code} - $errorBody")
                Result.failure(Exception("Supabase sign-in failed: ${response.code} - $errorBody"))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during Supabase sign-in", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Supabase sign-in", e)
            Result.failure(e)
        }
    }
}
