package com.pmis.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmis.app.utils.ResumeExtractor
import com.pmis.app.utils.ExtractedResumeInfo
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
    val extractedData: ExtractedResumeInfo? = null
)

class ResumeUploadViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResumeUploadState())
    val uiState: StateFlow<ResumeUploadState> = _uiState.asStateFlow()
    
    private lateinit var resumeExtractor: ResumeExtractor
    
    fun initializeExtractor(context: Context) {
        resumeExtractor = ResumeExtractor(context)
    }
    
    fun uploadAndExtractResume(uri: Uri) {
        if (!::resumeExtractor.isInitialized) {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "Resume extractor not initialized"
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
                val result = resumeExtractor.extractResumeData(uri)
                
                result.fold(
                    onSuccess = { extractedData ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Resume processed successfully! Data has been extracted and filled.",
                            extractedData = extractedData
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = when {
                                exception.message?.contains("selectable text") == true -> 
                                    "This PDF appears to be scanned or image-based. Please upload a PDF with selectable text."
                                exception.message?.contains("no pages") == true -> 
                                    "The PDF file is empty or corrupted. Please upload a valid PDF file."
                                else -> 
                                    "Failed to process resume: ${exception.message ?: "Unknown error"}"
                            }
                        )
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "An unexpected error occurred: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
    
    fun clearState() {
        _uiState.value = ResumeUploadState()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            isError = false,
            errorMessage = ""
        )
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
            successMessage = ""
        )
    }
}
