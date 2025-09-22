package com.pmis.app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.pmis.app.api.PDFExtractorAPI
import com.pmis.app.api.ExtractedResumeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ResumeUploadHelper(private val context: Context) {
    
    private val pdfExtractorAPI = PDFExtractorAPI(context)
    
    // Callback interfaces for UI updates
    interface LoadingCallback {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
    }
    
    interface MessageCallback {
        fun showSuccessMessage(message: String)
        fun showErrorMessage(message: String)
    }
    
    interface FormFiller {
        fun fillBasicInfo(name: String, email: String, phoneNumber: String)
        fun fillAcademicBackground(degree: String, institution: String, graduationYear: String)
        fun fillLocation(location: String)
        fun fillSkillsAndExperience(skills: List<String>, experience: String)
    }
    
    fun uploadResume(
        pdfFile: File,
        loadingCallback: LoadingCallback,
        messageCallback: MessageCallback,
        formFiller: FormFiller
    ) {
        // Show loading indicator
        loadingCallback.showLoadingIndicator()
        
        // Convert File to Uri and extract data
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val uri = Uri.fromFile(pdfFile)
                val extractedData = pdfExtractorAPI.extractData(uri)
                
                if (extractedData != null) {
                    // Automatically fill the extracted fields
                    formFiller.fillBasicInfo(
                        extractedData.name,
                        extractedData.email,
                        extractedData.phoneNumber
                    )
                    
                    formFiller.fillAcademicBackground(
                        extractedData.degree,
                        extractedData.institution,
                        extractedData.graduationYear
                    )
                    
                    formFiller.fillLocation(extractedData.location)
                    
                    formFiller.fillSkillsAndExperience(
                        extractedData.skills,
                        extractedData.experience
                    )
                    
                    // Hide loading indicator and show success
                    loadingCallback.hideLoadingIndicator()
                    messageCallback.showSuccessMessage("Resume data extracted and filled successfully!")
                    
                } else {
                    // Show error message if extraction failed
                    loadingCallback.hideLoadingIndicator()
                    messageCallback.showErrorMessage("Failed to extract data from the uploaded resume. Please try again.")
                }
                
            } catch (e: Exception) {
                Log.e("ResumeUploadHelper", "Error uploading resume", e)
                loadingCallback.hideLoadingIndicator()
                messageCallback.showErrorMessage("An error occurred while processing the resume: ${e.message}")
            }
        }
    }
    
    // Alternative method that matches your exact function signature
    fun uploadResume(
        pdfFile: File,
        onLoadingChange: (Boolean) -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onFormFill: (ExtractedResumeData) -> Unit
    ) {
        // Show loading indicator
        onLoadingChange(true)
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val uri = Uri.fromFile(pdfFile)
                val extractedData = pdfExtractorAPI.extractData(uri)
                
                if (extractedData != null) {
                    // Call form filler callback
                    onFormFill(extractedData)
                    
                    // Hide loading indicator and show success
                    onLoadingChange(false)
                    onSuccess("Resume data extracted and filled successfully!")
                    
                } else {
                    // Show error message if extraction failed
                    onLoadingChange(false)
                    onError("Failed to extract data from the uploaded resume. Please try again.")
                }
                
            } catch (e: Exception) {
                Log.e("ResumeUploadHelper", "Error uploading resume", e)
                onLoadingChange(false)
                onError("An error occurred while processing the resume: ${e.message}")
            }
        }
    }
}
