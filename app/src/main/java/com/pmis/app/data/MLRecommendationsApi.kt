package com.pmis.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * ML Recommendations API Client
 * Integrates with the ML Recommendations API hosted on Railway
 * API Documentation: https://web-production-c72b1.up.railway.app/docs
 */
object MLRecommendationsApi {
    
    private const val BASE_URL = "https://web-production-c72b1.up.railway.app"
    private const val TAG = "MLRecommendationsApi"
    
    /**
     * Data classes for API request and response
     */
    data class RecommendationRequest(
        val student_id: String,
        val skills: List<String>,
        val stream: String,
        val cgpa: Double,
        val rural_urban: String,
        val college_tier: String
    )
    
    data class CourseInfo(
        val name: String,
        val url: String,
        val platform: String
    )
    
    data class Recommendation(
        val internship_id: String,
        val title: String,
        val organization_name: String,
        val domain: String,
        val location: String,
        val duration: String,
        val stipend: Double,
        val success_prob: Double,
        val missing_skills: List<String>,
        val courses: List<CourseInfo>,
        val reasons: List<String>
    )
    
    data class RecommendationResponse(
        val student_id: String,
        val total_recommendations: Int,
        val recommendations: List<Recommendation>,
        val generated_at: String
    )
    
    /**
     * Check API health status
     */
    suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Checking API health...")
        try {
            val url = URL("$BASE_URL/health")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            
            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream.bufferedReader().use { it.readText() }
            }
            
            Log.d(TAG, "Health check response: $responseCode - $response")
            connection.disconnect()
            
            responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            Log.e(TAG, "Health check failed", e)
            false
        }
    }
    
    /**
     * Get personalized internship recommendations
     */
    suspend fun getRecommendations(request: RecommendationRequest): Result<RecommendationResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Requesting recommendations for student: ${request.student_id}")
            
            val url = URL("$BASE_URL/recommendations")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            
            // Create request JSON
            val requestJson = JSONObject().apply {
                put("student_id", request.student_id)
                put("skills", JSONArray(request.skills))
                put("stream", request.stream)
                put("cgpa", request.cgpa)
                put("rural_urban", request.rural_urban)
                put("college_tier", request.college_tier)
            }
            
            // Send request
            connection.outputStream.use { outputStream ->
                outputStream.write(requestJson.toString().toByteArray())
            }
            
            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream.bufferedReader().use { it.readText() }
            }
            
            Log.d(TAG, "Recommendations response: $responseCode - $response")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseJson = JSONObject(response)
                val recommendations = parseRecommendationsResponse(responseJson)
                Result.success(recommendations)
            } else {
                Log.e(TAG, "API request failed with code: $responseCode, response: $response")
                Result.failure(IOException("API request failed: $responseCode - $response"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get recommendations", e)
            Result.failure(e)
        }
    }
    
    /**
     * Parse the JSON response into RecommendationResponse object
     */
    private fun parseRecommendationsResponse(json: JSONObject): RecommendationResponse {
        val recommendationsArray = json.getJSONArray("recommendations")
        val recommendations = mutableListOf<Recommendation>()
        
        for (i in 0 until recommendationsArray.length()) {
            val recJson = recommendationsArray.getJSONObject(i)
            val coursesArray = recJson.getJSONArray("courses")
            val courses = mutableListOf<CourseInfo>()
            
            for (j in 0 until coursesArray.length()) {
                val courseJson = coursesArray.getJSONObject(j)
                courses.add(
                    CourseInfo(
                        name = courseJson.getString("name"),
                        url = courseJson.getString("url"),
                        platform = courseJson.getString("platform")
                    )
                )
            }
            
            val missingSkillsArray = recJson.getJSONArray("missing_skills")
            val missingSkills = mutableListOf<String>()
            for (j in 0 until missingSkillsArray.length()) {
                missingSkills.add(missingSkillsArray.getString(j))
            }
            
            val reasonsArray = recJson.getJSONArray("reasons")
            val reasons = mutableListOf<String>()
            for (j in 0 until reasonsArray.length()) {
                reasons.add(reasonsArray.getString(j))
            }
            
            recommendations.add(
                Recommendation(
                    internship_id = recJson.getString("internship_id"),
                    title = recJson.getString("title"),
                    organization_name = recJson.getString("organization_name"),
                    domain = recJson.getString("domain"),
                    location = recJson.getString("location"),
                    duration = recJson.getString("duration"),
                    stipend = recJson.getDouble("stipend"),
                    success_prob = recJson.getDouble("success_prob"),
                    missing_skills = missingSkills,
                    courses = courses,
                    reasons = reasons
                )
            )
        }
        
        return RecommendationResponse(
            student_id = json.getString("student_id"),
            total_recommendations = json.getInt("total_recommendations"),
            recommendations = recommendations,
            generated_at = json.getString("generated_at")
        )
    }
    
    /**
     * Generate a unique student ID based on current timestamp
     */
    fun generateStudentId(): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return "STU_$timestamp"
    }
    
    /**
     * Helper function to map college name to tier
     */
    fun mapCollegeToTier(collegeName: String): String {
        val name = collegeName.lowercase()
        return when {
            name.contains("iit") || name.contains("nit") || name.contains("iiit") -> "Tier-1"
            name.contains("university") || name.contains("college") -> "Tier-2"
            else -> "Tier-3"
        }
    }
    
    /**
     * Helper function to determine rural/urban based on location
     */
    fun mapLocationToRuralUrban(location: String): String {
        val metroCities = listOf("mumbai", "delhi", "bangalore", "hyderabad", "chennai", "kolkata", "pune", "ahmedabad")
        val locationLower = location.lowercase()
        
        return if (metroCities.any { locationLower.contains(it) }) {
            "Urban"
        } else {
            "Rural"
        }
    }
}
