package com.pmis.app.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmis.app.utils.DocumentExtractor
import com.pmis.app.api.ExtractedResumeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResumeUploadState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = "",
    val extractedData: ExtractedResumeData? = null
)

class ResumeUploadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResumeUploadState())
    val uiState: StateFlow<ResumeUploadState> = _uiState.asStateFlow()

    private lateinit var documentExtractor: DocumentExtractor
    private lateinit var context: Context

    fun initializeExtractor(context: Context) {
        this.context = context
        documentExtractor = DocumentExtractor(context)
    }
    
    fun uploadAndExtractResume(uri: Uri) {
        if (!::documentExtractor.isInitialized || !::context.isInitialized) {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "Document extractor not initialized"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isError = false,
                isSuccess = false,
                errorMessage = "",
                successMessage = ""
            )

            try {
                Log.d("ResumeUploadViewModel", "Starting resume extraction from URI: $uri")
                
                // Validate file type first - with error handling
                val mimeType = try {
                    context.contentResolver.getType(uri)
                } catch (e: Exception) {
                    Log.w("ResumeUploadViewModel", "Could not get MIME type", e)
                    null
                }
                
                if (mimeType != null && mimeType != "application/pdf") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Please select a valid PDF file."
                    )
                    return@launch
                }
                
                // Extract data using local DocumentExtractor
                val extractedData = documentExtractor.extractFromUri(uri)
                
                // Check if extraction failed due to scanned PDF
                if (isScannedPDFError(extractedData)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Upload a PDF with selectable text."
                    )
                    return@launch
                }
                
                // Convert the extracted data to the expected format
                val convertedData = convertToExtractedResumeData(extractedData)

                if (isValidExtractedData(convertedData)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Resume processed successfully! Data has been extracted and filled.",
                        extractedData = convertedData
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Resume has no selectable text. Upload a text-based PDF."
                    )
                }

            } catch (e: Exception) {
                Log.e("ResumeUploadViewModel", "Error extracting resume data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Could not read file. Try again."
                )
            }
        }
    }
    
    private fun convertToExtractedResumeData(extractedData: com.pmis.app.utils.ExtractedResumeData): ExtractedResumeData {
        return ExtractedResumeData(
            name = extractNameFromText(extractedData.education + " " + extractedData.experience),
            email = extractEmailFromText(extractedData.education + " " + extractedData.experience),
            phoneNumber = extractPhoneFromText(extractedData.education + " " + extractedData.experience),
            location = extractLocationFromText(extractedData.education + " " + extractedData.experience),
            degree = extractDegreeFromText(extractedData.education),
            institution = extractInstitutionFromText(extractedData.education),
            graduationYear = extractGraduationYearFromText(extractedData.education),
            skills = extractSkillsListFromText(extractedData.skills),
            experience = extractedData.experience,
            cgpa = extractCGPAFromText(extractedData.education),
            stream = extractStreamFromText(extractedData.education)
        )
    }
    
    private fun isValidExtractedData(data: ExtractedResumeData): Boolean {
        return data.name.isNotEmpty() || 
               data.email.isNotEmpty() || 
               data.phoneNumber.isNotEmpty() || 
               data.degree.isNotEmpty() || 
               data.institution.isNotEmpty() ||
               data.skills.isNotEmpty() ||
               data.experience.isNotEmpty()
    }
    
    private fun isScannedPDFError(extractedData: com.pmis.app.utils.ExtractedResumeData): Boolean {
        // Check if all extracted fields are empty, indicating a scanned PDF
        return extractedData.education.isEmpty() && 
               extractedData.skills.isEmpty() && 
               extractedData.experience.isEmpty()
    }
    
    // Helper methods to extract specific fields from text
    private fun extractNameFromText(text: String): String {
        val lines = text.lines()
        return lines.firstOrNull { line -> 
            line.trim().length > 2 && 
            line.trim().length < 50 && 
            !line.contains("@") && 
            !line.contains("http") &&
            line.matches(Regex(".*[a-zA-Z].*"))
        }?.trim() ?: ""
    }
    
    private fun extractEmailFromText(text: String): String {
        val emailRegex = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")
        return emailRegex.find(text)?.value ?: ""
    }
    
    private fun extractPhoneFromText(text: String): String {
        val phoneRegex = Regex("\\b(\\+?[0-9]{10,15})\\b")
        return phoneRegex.find(text)?.value ?: ""
    }
    
    private fun extractLocationFromText(text: String): String {
        val locationKeywords = listOf("city", "state", "country", "address", "location")
        val lines = text.lines()
        return lines.firstOrNull { line ->
            locationKeywords.any { keyword -> line.lowercase().contains(keyword) }
        }?.trim() ?: ""
    }
    
    private fun extractDegreeFromText(text: String): String {
        val degreeKeywords = listOf("bachelor", "master", "phd", "diploma", "certificate", "btech", "mtech", "bsc", "msc", "ba", "ma", "be", "me", "bca", "mca")
        val lines = text.lines()
        return lines.firstOrNull { line ->
            degreeKeywords.any { keyword -> line.lowercase().contains(keyword) }
        }?.trim() ?: ""
    }
    
    private fun extractInstitutionFromText(text: String): String {
        val institutionKeywords = listOf("university", "college", "institute", "school")
        val lines = text.lines()
        return lines.firstOrNull { line ->
            institutionKeywords.any { keyword -> line.lowercase().contains(keyword) }
        }?.trim() ?: ""
    }
    
    private fun extractGraduationYearFromText(text: String): String {
        val yearRegex = Regex("\\b(19|20)\\d{2}\\b")
        return yearRegex.find(text)?.value ?: ""
    }
    
    private fun extractSkillsListFromText(text: String): List<String> {
        val commonSkills = listOf(
            "python", "java", "javascript", "react", "angular", "vue", "node", "express",
            "sql", "mysql", "postgresql", "mongodb", "redis", "docker", "kubernetes",
            "aws", "azure", "gcp", "git", "github", "gitlab", "jenkins", "ci/cd",
            "android", "ios", "flutter", "react native", "kotlin", "swift",
            "machine learning", "ai", "data science", "analytics", "tableau", "power bi",
            "html", "css", "bootstrap", "sass", "less", "webpack", "babel",
            "spring", "django", "flask", "laravel", "rails", "asp.net", "php",
            "c++", "c#", "go", "rust", "scala", "ruby", "perl", "r", "matlab"
        )
        
        val foundSkills = mutableListOf<String>()
        val lowerText = text.lowercase()
        
        for (skill in commonSkills) {
            if (lowerText.contains(skill)) {
                foundSkills.add(skill)
            }
        }
        
        return foundSkills.take(10) // Limit to 10 skills
    }
    
    private fun extractCGPAFromText(text: String): String {
        val cgpaRegex = Regex("\\b(\\d+\\.?\\d*)\\s*(cgpa|gpa)\\b", RegexOption.IGNORE_CASE)
        return cgpaRegex.find(text)?.groupValues?.get(1) ?: ""
    }
    
    private fun extractStreamFromText(text: String): String {
        val streamKeywords = listOf("computer science", "information technology", "electronics", "mechanical", "civil", "electrical", "chemical", "biotechnology")
        val lines = text.lines()
        return lines.firstOrNull { line ->
            streamKeywords.any { keyword -> line.lowercase().contains(keyword) }
        }?.trim() ?: ""
    }
    
    fun clearState() {
        _uiState.value = ResumeUploadState()
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, successMessage = "")
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(isError = false, errorMessage = "")
    }
}

