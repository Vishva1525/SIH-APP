package com.pmis.app.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class ExtractedResumeData(
    val education: String,
    val skills: String,
    val experience: String
)

class DocumentExtractor(private val context: Context) {
    
    suspend fun extractFromUri(uri: Uri): ExtractedResumeData = withContext(Dispatchers.IO) {
        try {
            val content = readTextFromUri(uri)
            parseResumeContent(content)
        } catch (e: Exception) {
            ExtractedResumeData(
                education = "Error extracting education: ${e.message}",
                skills = "Error extracting skills: ${e.message}",
                experience = "Error extracting experience: ${e.message}"
            )
        }
    }
    
    private fun readTextFromUri(uri: Uri): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            
            reader.close()
            inputStream?.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }
    
    private fun parseResumeContent(content: String): ExtractedResumeData {
        val lines = content.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        
        val education = extractSection(lines, listOf("education", "academic", "qualification", "degree", "university", "college"))
        val skills = extractSection(lines, listOf("skills", "technical skills", "programming", "languages", "technologies", "tools"))
        val experience = extractSection(lines, listOf("experience", "work experience", "employment", "career", "job", "professional"))
        
        return ExtractedResumeData(
            education = education.ifEmpty { "No education information found. Please add manually." },
            skills = skills.ifEmpty { "No skills information found. Please add manually." },
            experience = experience.ifEmpty { "No experience information found. Please add manually." }
        )
    }
    
    private fun extractSection(lines: List<String>, keywords: List<String>): String {
        val result = StringBuilder()
        var inSection = false
        var sectionDepth = 0
        
        for (line in lines) {
            val lowerLine = line.lowercase()
            
            // Check if this line starts a new section
            val startsNewSection = keywords.any { keyword ->
                lowerLine.contains(keyword) || lowerLine.startsWith(keyword)
            }
            
            if (startsNewSection) {
                inSection = true
                sectionDepth = 0
                result.append("$line\n")
                continue
            }
            
            // If we're in a section, collect content
            if (inSection) {
                // Check if this might be the start of a different section
                val isOtherSection = line.isNotEmpty() && 
                    !line.startsWith(" ") && 
                    !line.startsWith("-") && 
                    !line.startsWith("•") &&
                    !line.matches(Regex(".*\\d{4}.*")) && // Not a date
                    !line.matches(Regex(".*@.*")) && // Not an email
                    !line.matches(Regex(".*\\d{10}.*")) // Not a phone number
                
                if (isOtherSection && sectionDepth > 2) {
                    // Probably a new section, stop here
                    break
                }
                
                result.append("$line\n")
                sectionDepth++
                
                // Stop after collecting reasonable amount of content
                if (sectionDepth > 10) {
                    break
                }
            }
        }
        
        return result.toString().trim()
    }
}
