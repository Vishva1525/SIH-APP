package com.pmis.app.api

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

data class ExtractedResumeData(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val degree: String = "",
    val institution: String = "",
    val graduationYear: String = "",
    val skills: List<String> = emptyList(),
    val experience: String = "",
    val cgpa: String = "",
    val stream: String = ""
)

class PDFExtractorAPI(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "PMIS-App/1.0")
                .build()
            chain.proceed(request)
        }
        .build()
    
    // Replace with your actual PDFExtractor API endpoint
    private val baseUrl = "https://api.pdfextractor.com/v1" // Example endpoint
    private val apiKey = "your-api-key-here" // Replace with your actual API key
    
    suspend fun extractData(uri: Uri): ExtractedResumeData? = withContext(Dispatchers.IO) {
        try {
            Log.d("PDFExtractorAPI", "Starting PDF extraction from URI: $uri")
            
            // Convert URI to File
            val file = uriToFile(uri) ?: return@withContext null
            
            // Upload file to PDFExtractor API
            val extractedData = uploadAndExtract(file)
            
            // Clean up temporary file
            file.delete()
            
            extractedData
            
        } catch (e: Exception) {
            Log.e("PDFExtractorAPI", "Error extracting PDF data", e)
            null
        }
    }
    
    private suspend fun uploadAndExtract(file: File): ExtractedResumeData? = withContext(Dispatchers.IO) {
        try {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("application/pdf".toMediaType())
                )
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("extract_fields", "name,email,phone,location,education,skills,experience")
                .build()
            
            val request = Request.Builder()
                .url("$baseUrl/extract")
                .post(requestBody)
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("PDFExtractorAPI", "API Response: $responseBody")
                
                // Parse the response (adjust based on your API response format)
                parseExtractedData(responseBody ?: "")
            } else {
                Log.e("PDFExtractorAPI", "API request failed: ${response.code} - ${response.message}")
                null
            }
            
        } catch (e: Exception) {
            Log.e("PDFExtractorAPI", "Error uploading file to API", e)
            null
        }
    }
    
    private fun parseExtractedData(jsonResponse: String): ExtractedResumeData? {
        return try {
            // Parse JSON response (adjust based on your API response format)
            // This is a simplified example - you'll need to adjust based on your actual API response
            
            // Example JSON structure:
            // {
            //   "success": true,
            //   "data": {
            //     "name": "John Doe",
            //     "email": "john@example.com",
            //     "phone": "+1234567890",
            //     "location": "New York",
            //     "education": {
            //       "degree": "Bachelor of Computer Science",
            //       "institution": "University of Technology",
            //       "graduation_year": "2023"
            //     },
            //     "skills": ["Python", "Java", "React"],
            //     "experience": "Software Developer at Tech Corp..."
            //   }
            // }
            
            // For now, return a mock response - replace with actual JSON parsing
            ExtractedResumeData(
                name = "Extracted Name",
                email = "extracted@email.com",
                phoneNumber = "+1234567890",
                location = "Extracted Location",
                degree = "Bachelor of Computer Science",
                institution = "Extracted University",
                graduationYear = "2023",
                skills = listOf("Python", "Java", "React"),
                experience = "Extracted work experience...",
                cgpa = "8.5",
                stream = "Computer Science"
            )
            
        } catch (e: Exception) {
            Log.e("PDFExtractorAPI", "Error parsing extracted data", e)
            null
        }
    }
    
    private suspend fun uriToFile(uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("PDFExtractorAPI", "Cannot open input stream from URI")
                return@withContext null
            }
            
            val tempFile = File.createTempFile("resume_", ".pdf", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
            Log.d("PDFExtractorAPI", "File created: ${tempFile.absolutePath}")
            tempFile
            
        } catch (e: Exception) {
            Log.e("PDFExtractorAPI", "Error converting URI to file", e)
            null
        }
    }
}
