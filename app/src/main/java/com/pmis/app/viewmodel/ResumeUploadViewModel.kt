package com.pmis.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmis.app.api.PDFExtractorAPI
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
    
    private lateinit var pdfExtractorAPI: PDFExtractorAPI
    
    fun initializeExtractor(context: Context) {
        pdfExtractorAPI = PDFExtractorAPI(context)
    }
    
    fun uploadAndExtractResume(uri: Uri) {
        if (!::pdfExtractorAPI.isInitialized) {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "PDF extractor not initialized"
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
                val extractedData = pdfExtractorAPI.extractData(uri)
                
                if (extractedData != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Resume processed successfully! Data has been extracted and filled.",
                        extractedData = extractedData
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to extract data from the uploaded resume. Please try again."
                    )
                }
                
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
