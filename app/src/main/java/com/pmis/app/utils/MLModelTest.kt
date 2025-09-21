package com.pmis.app.utils

import android.util.Log
import com.pmis.app.data.MLRecommendationsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Test utility for ML Recommendations API
 */
object MLModelTest {
    private const val TAG = "MLModelTest"
    
    /**
     * Test the ML API with sample data
     */
    fun testMLAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Starting ML API test...")
                
                // Test 1: Health Check
                Log.d(TAG, "=== Testing API Health ===")
                val isHealthy = MLRecommendationsApi.checkHealth()
                Log.d(TAG, "API Health Status: $isHealthy")
                
                if (!isHealthy) {
                    Log.e(TAG, "API is not healthy, skipping recommendation test")
                    return@launch
                }
                
                // Test 2: Sample Recommendation Request
                Log.d(TAG, "=== Testing Sample Recommendation ===")
                val sampleRequest = MLRecommendationsApi.RecommendationRequest(
                    student_id = MLRecommendationsApi.generateStudentId(),
                    skills = listOf("Python", "Machine Learning", "Data Analysis", "SQL"),
                    stream = "Computer Science",
                    cgpa = 8.5,
                    rural_urban = "Urban",
                    college_tier = "Tier-1"
                )
                
                Log.d(TAG, "Sample Request: $sampleRequest")
                
                val result = MLRecommendationsApi.getRecommendations(sampleRequest)
                
                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "=== API Test SUCCESS ===")
                        Log.d(TAG, "Student ID: ${response.student_id}")
                        Log.d(TAG, "Total Recommendations: ${response.total_recommendations}")
                        Log.d(TAG, "Generated At: ${response.generated_at}")
                        
                        response.recommendations.forEachIndexed { index, rec ->
                            Log.d(TAG, "Recommendation ${index + 1}:")
                            Log.d(TAG, "  Title: ${rec.title}")
                            Log.d(TAG, "  Organization: ${rec.organization_name}")
                            Log.d(TAG, "  Domain: ${rec.domain}")
                            Log.d(TAG, "  Location: ${rec.location}")
                            Log.d(TAG, "  Duration: ${rec.duration}")
                            Log.d(TAG, "  Stipend: ₹${rec.stipend}")
                            Log.d(TAG, "  Success Probability: ${rec.success_prob}")
                            Log.d(TAG, "  Missing Skills: ${rec.missing_skills}")
                            Log.d(TAG, "  Reasons: ${rec.reasons}")
                            Log.d(TAG, "  Courses: ${rec.courses.map { "${it.name} (${it.platform})" }}")
                            Log.d(TAG, "  ---")
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "=== API Test FAILED ===")
                        Log.e(TAG, "Error: ${error.message}", error)
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Test failed with exception", e)
            }
        }
    }
    
    /**
     * Test with different scenarios
     */
    fun testMultipleScenarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val testCases = listOf(
                // Case 1: High-performing CS student
                MLRecommendationsApi.RecommendationRequest(
                    student_id = MLRecommendationsApi.generateStudentId(),
                    skills = listOf("Python", "Java", "React", "Node.js", "AWS", "Docker"),
                    stream = "Computer Science",
                    cgpa = 9.2,
                    rural_urban = "Urban",
                    college_tier = "Tier-1"
                ),
                
                // Case 2: Data Science student
                MLRecommendationsApi.RecommendationRequest(
                    student_id = MLRecommendationsApi.generateStudentId(),
                    skills = listOf("Python", "Pandas", "Scikit-learn", "TensorFlow", "SQL"),
                    stream = "Data Science",
                    cgpa = 8.0,
                    rural_urban = "Urban",
                    college_tier = "Tier-2"
                ),
                
                // Case 3: Rural student with basic skills
                MLRecommendationsApi.RecommendationRequest(
                    student_id = MLRecommendationsApi.generateStudentId(),
                    skills = listOf("HTML", "CSS", "JavaScript"),
                    stream = "Information Technology",
                    cgpa = 7.5,
                    rural_urban = "Rural",
                    college_tier = "Tier-3"
                )
            )
            
            testCases.forEachIndexed { index, request ->
                Log.d(TAG, "=== Testing Scenario ${index + 1} ===")
                Log.d(TAG, "Skills: ${request.skills}")
                Log.d(TAG, "Stream: ${request.stream}")
                Log.d(TAG, "CGPA: ${request.cgpa}")
                Log.d(TAG, "Location: ${request.rural_urban}")
                Log.d(TAG, "College Tier: ${request.college_tier}")
                
                val result = MLRecommendationsApi.getRecommendations(request)
                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "SUCCESS: Got ${response.total_recommendations} recommendations")
                        response.recommendations.take(2).forEach { rec ->
                            Log.d(TAG, "  - ${rec.title} at ${rec.organization_name} (${rec.success_prob}% match)")
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "FAILED: ${error.message}")
                    }
                )
                
                // Add delay between requests
                kotlinx.coroutines.delay(1000)
            }
        }
    }
}
