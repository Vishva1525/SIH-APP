package com.pmis.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pmis.app.screens.InternFormState
import java.util.*

/**
 * Enhanced global app state for comprehensive data management
 */
object EnhancedAppState {
    // User Profile
    var userProfile by mutableStateOf(UserProfile())
        private set
    
    // Intern Form Data
    var internFormData by mutableStateOf(InternFormState())
        private set
    
    // Notifications
    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set
    
    // Recommendations
    var recommendations by mutableStateOf<List<InternshipRecommendation>>(emptyList())
        private set
    
    // Applications
    var applications by mutableStateOf<List<Application>>(emptyList())
        private set
    
    // App Settings
    var appSettings by mutableStateOf(AppSettings())
        private set
    
    // Loading States
    var isLoadingRecommendations by mutableStateOf(false)
        private set
    
    var isLoadingApplications by mutableStateOf(false)
        private set
    
    // Error States
    var lastError by mutableStateOf<String?>(null)
        private set
    
    // User Profile Management
    fun updateUserProfile(newProfile: UserProfile) {
        userProfile = newProfile
    }
    
    fun clearUserProfile() {
        userProfile = UserProfile()
    }
    
    // Intern Form Management
    fun updateInternFormData(newData: InternFormState) {
        internFormData = newData
    }
    
    fun clearInternFormData() {
        internFormData = InternFormState()
    }
    
    // Notifications Management
    fun addNotification(notification: Notification) {
        notifications = notifications + notification
    }
    
    fun markNotificationAsRead(notificationId: String) {
        notifications = notifications.map { 
            if (it.id == notificationId) it.copy(isRead = true) else it 
        }
    }
    
    fun markAllNotificationsAsRead() {
        notifications = notifications.map { it.copy(isRead = true) }
    }
    
    fun clearNotifications() {
        notifications = emptyList()
    }
    
    // Recommendations Management
    fun updateRecommendations(newRecommendations: List<InternshipRecommendation>) {
        recommendations = newRecommendations
    }
    
    fun addRecommendation(recommendation: InternshipRecommendation) {
        recommendations = recommendations + recommendation
    }
    
    fun clearRecommendations() {
        recommendations = emptyList()
    }
    
    // Applications Management
    fun addApplication(application: Application) {
        applications = applications + application
    }
    
    fun updateApplicationStatus(applicationId: String, newStatus: ApplicationStatus) {
        applications = applications.map { 
            if (it.id == applicationId) it.copy(status = newStatus) else it 
        }
    }
    
    fun clearApplications() {
        applications = emptyList()
    }
    
    // Settings Management
    fun updateAppSettings(newSettings: AppSettings) {
        appSettings = newSettings
    }
    
    // Loading States
    fun updateLoadingRecommendations(loading: Boolean) {
        isLoadingRecommendations = loading
    }
    
    fun updateLoadingApplications(loading: Boolean) {
        isLoadingApplications = loading
    }
    
    // Error Management
    fun setError(error: String?) {
        lastError = error
    }
    
    fun clearError() {
        lastError = null
    }
    
    // Utility Functions
    fun getUnreadNotificationCount(): Int {
        return notifications.count { !it.isRead }
    }
    
    fun getActiveApplicationsCount(): Int {
        return applications.count { it.status == ApplicationStatus.SUBMITTED || it.status == ApplicationStatus.UNDER_REVIEW }
    }
    
    fun getCompletedApplicationsCount(): Int {
        return applications.count { it.status == ApplicationStatus.ACCEPTED || it.status == ApplicationStatus.REJECTED }
    }
}

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val collegeName: String = "",
    val yearOfStudy: String = "",
    val profilePicture: String = "",
    val isVerified: Boolean = false,
    val createdAt: Date = Date(),
    val lastUpdated: Date = Date()
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Date,
    val type: NotificationType,
    val isRead: Boolean = false,
    val action: String? = null,
    val data: Map<String, String> = emptyMap()
)

enum class NotificationType {
    RECOMMENDATION, APPLICATION, REMINDER, UPDATE, SUCCESS, ERROR
}

data class InternshipRecommendation(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val duration: String,
    val type: String,
    val matchScore: Int,
    val description: String,
    val requirements: List<String>,
    val benefits: List<String>,
    val skills: List<String>,
    val salary: String? = null,
    val isRemote: Boolean = false,
    val isUrgent: Boolean = false,
    val applicationDeadline: Date? = null,
    val companyLogo: String? = null,
    // Additional properties for popup
    val internship_id: String = id,
    val organization_name: String = company,
    val domain: String = type,
    val stipend: Int = salary?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0,
    val success_prob: Float = matchScore / 100f,
    val missing_skills: List<String> = emptyList(),
    val course_suggestions: List<CourseSuggestion> = emptyList(),
    val reasons: List<String> = emptyList(),
    val application_deadline: String = applicationDeadline?.toString() ?: "",
    val rank: Int = 1,
    val company_logo_url: String? = companyLogo,
    val contactEmail: String? = null
)

data class CourseSuggestion(
    val name: String,
    val platform: String,
    val duration_estimate: String,
    val description: String,
    val enrollment_url: String
)

data class Application(
    val id: String,
    val internshipId: String,
    val internshipTitle: String,
    val companyName: String,
    val appliedDate: Date,
    val status: ApplicationStatus,
    val notes: String = "",
    val documents: List<String> = emptyList(),
    val interviewDate: Date? = null,
    val feedback: String? = null
)

enum class ApplicationStatus {
    DRAFT, SUBMITTED, UNDER_REVIEW, INTERVIEW_SCHEDULED, ACCEPTED, REJECTED, WITHDRAWN
}

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val darkMode: Boolean = false,
    val language: String = "en",
    val autoSave: Boolean = true,
    val dataUsage: DataUsageSettings = DataUsageSettings()
)

data class DataUsageSettings(
    val allowAnalytics: Boolean = true,
    val allowCrashReporting: Boolean = true,
    val allowPersonalization: Boolean = true
)

// Extension functions for better data handling
fun List<Notification>.getUnreadCount(): Int = count { !it.isRead }

fun List<Application>.getByStatus(status: ApplicationStatus): List<Application> = 
    filter { it.status == status }

fun List<InternshipRecommendation>.getHighMatch(): List<InternshipRecommendation> = 
    filter { it.matchScore >= 80 }

fun List<InternshipRecommendation>.getRemote(): List<InternshipRecommendation> = 
    filter { it.isRemote }

fun List<InternshipRecommendation>.getUrgent(): List<InternshipRecommendation> = 
    filter { it.isUrgent }

