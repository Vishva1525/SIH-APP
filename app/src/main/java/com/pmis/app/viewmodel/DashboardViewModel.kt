package com.pmis.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Data classes for dashboard
data class DashboardSummary(
    val activeApplicationsCount: Int = 0,
    val recommendationsCount: Int = 0,
    val profileCompletion: Float = 0f,
    val profileViews: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ActivityItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: ActivityType,
    val payload: Map<String, Any> = emptyMap()
)

data class ApplicationItem(
    val id: String,
    val title: String,
    val company: String,
    val status: String,
    val appliedDate: String
)

data class RecommendationItem(
    val id: String,
    val title: String,
    val company: String,
    val matchScore: Float,
    val location: String
)

enum class ActivityType {
    INTERNSHIP, RECOMMENDATION, APPLICATION, UPDATE
}

class DashboardViewModel : ViewModel() {
    
    private val _summary = MutableStateFlow(DashboardSummary(isLoading = true))
    val summary: StateFlow<DashboardSummary> = _summary.asStateFlow()
    
    private val _activities = MutableStateFlow<List<ActivityItem>>(emptyList())
    val activities: StateFlow<List<ActivityItem>> = _activities.asStateFlow()
    
    private val _applications = MutableStateFlow<List<ApplicationItem>>(emptyList())
    val applications: StateFlow<List<ApplicationItem>> = _applications.asStateFlow()
    
    private val _recommendations = MutableStateFlow<List<RecommendationItem>>(emptyList())
    val recommendations: StateFlow<List<RecommendationItem>> = _recommendations.asStateFlow()
    
    init {
        refreshDashboard()
    }
    
    fun refreshDashboard() {
        viewModelScope.launch {
            _summary.value = _summary.value.copy(isLoading = true, error = null)
            
            try {
                // Simulate API calls with delay
                val summaryData = withContext(Dispatchers.IO) {
                    delay(1000) // Simulate network delay
                    fetchDashboardSummary()
                }
                
                val activitiesData = withContext(Dispatchers.IO) {
                    delay(500)
                    fetchRecentActivities()
                }
                
                _summary.value = summaryData.copy(isLoading = false)
                _activities.value = activitiesData
                
            } catch (e: Exception) {
                _summary.value = _summary.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }
    
    fun refreshApplications() {
        viewModelScope.launch {
            try {
                val applicationsData = withContext(Dispatchers.IO) {
                    delay(800)
                    fetchActiveApplications()
                }
                _applications.value = applicationsData
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun refreshRecommendations() {
        viewModelScope.launch {
            try {
                val recommendationsData = withContext(Dispatchers.IO) {
                    delay(800)
                    fetchRecommendations()
                }
                _recommendations.value = recommendationsData
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun getRecommendations(source: String = "dashboard") {
        viewModelScope.launch {
            try {
                val recommendationsData = withContext(Dispatchers.IO) {
                    delay(800)
                    fetchRecommendations(source)
                }
                _recommendations.value = recommendationsData
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // Mock API functions - replace with actual API calls
    private suspend fun fetchDashboardSummary(): DashboardSummary {
        return DashboardSummary(
            activeApplicationsCount = 3,
            recommendationsCount = 12,
            profileCompletion = 0.85f,
            profileViews = 47
        )
    }
    
    private suspend fun fetchRecentActivities(): List<ActivityItem> {
        return listOf(
            ActivityItem(
                id = "1",
                title = "New Recommendation",
                description = "Data Science internship at TechCorp",
                time = "2 hours ago",
                type = ActivityType.RECOMMENDATION,
                payload = mapOf("jobId" to "job_123", "companyId" to "techcorp_456")
            ),
            ActivityItem(
                id = "2",
                title = "Application Submitted",
                description = "Software Developer role at StartupXYZ",
                time = "1 day ago",
                type = ActivityType.APPLICATION,
                payload = mapOf("applicationId" to "app_789", "status" to "submitted")
            ),
            ActivityItem(
                id = "3",
                title = "Profile Updated",
                description = "Added new skills and experience",
                time = "3 days ago",
                type = ActivityType.UPDATE,
                payload = mapOf("updatedFields" to listOf("skills", "experience"))
            )
        )
    }
    
    private suspend fun fetchActiveApplications(): List<ApplicationItem> {
        return listOf(
            ApplicationItem(
                id = "app_1",
                title = "Software Developer Intern",
                company = "TechCorp",
                status = "Under Review",
                appliedDate = "2024-01-15"
            ),
            ApplicationItem(
                id = "app_2",
                title = "Data Science Intern",
                company = "StartupXYZ",
                status = "Interview Scheduled",
                appliedDate = "2024-01-12"
            ),
            ApplicationItem(
                id = "app_3",
                title = "Product Manager Intern",
                company = "InnovateLabs",
                status = "Application Submitted",
                appliedDate = "2024-01-10"
            )
        )
    }
    
    private suspend fun fetchRecommendations(source: String = "dashboard"): List<RecommendationItem> {
        return listOf(
            RecommendationItem(
                id = "rec_1",
                title = "Full Stack Developer Intern",
                company = "WebTech Solutions",
                matchScore = 0.95f,
                location = "Remote"
            ),
            RecommendationItem(
                id = "rec_2",
                title = "Machine Learning Intern",
                company = "AI Innovations",
                matchScore = 0.88f,
                location = "San Francisco, CA"
            ),
            RecommendationItem(
                id = "rec_3",
                title = "Mobile App Developer",
                company = "AppCraft",
                matchScore = 0.82f,
                location = "New York, NY"
            )
        )
    }
}
