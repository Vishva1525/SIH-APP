package com.pmis.app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
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
        
        val education = extractEducationDetails(lines)
        val skills = extractSkillsDetails(lines)
        val experience = extractExperienceDetails(lines)
        
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
    
    private fun extractEducationDetails(lines: List<String>): String {
        val educationKeywords = listOf("education", "academic", "qualification", "degree", "university", "college", "school")
        val degreeKeywords = listOf("b.tech", "btech", "bachelor", "master", "mtech", "m.tech", "phd", "diploma", "bsc", "msc", "ba", "ma", "bca", "mca", "be", "me")
        val institutionKeywords = listOf("university", "college", "institute", "school", "iit", "nit", "iiit")
        
        val educationEntries = mutableListOf<EducationEntry>()
        var inEducationSection = false
        var currentEntry: EducationEntry? = null
        
        for (i in lines.indices) {
            val line = lines[i]
            val lowerLine = line.lowercase()
            
            // Check if we're entering education section
            if (educationKeywords.any { keyword -> lowerLine.contains(keyword) && lowerLine.length < 50 }) {
                inEducationSection = true
                continue
            }
            
            // Check if we're leaving education section (new major section)
            if (inEducationSection && isNewMajorSection(line)) {
                break
            }
            
            if (inEducationSection) {
                // Try to extract education information from this line
                val extractedEntry = parseEducationLine(line)
                if (extractedEntry != null) {
                    educationEntries.add(extractedEntry)
                } else {
                    // Check if this line contains degree or institution info
                    val degree = extractDegreeFromLine(line, degreeKeywords)
                    val institution = extractInstitutionFromLine(line, institutionKeywords)
                    
                    if (degree.isNotEmpty() || institution.isNotEmpty()) {
                        if (currentEntry == null) {
                            currentEntry = EducationEntry("", "", "")
                        }
                        if (degree.isNotEmpty()) currentEntry.degree = degree
                        if (institution.isNotEmpty()) currentEntry.institution = institution
                        
                        // Try to extract year from this line or next lines
                        val year = extractYearFromLine(line) ?: 
                                  if (i < lines.size - 1) extractYearFromLine(lines[i + 1]) else null
                        if (year != null) currentEntry.year = year
                        
                        if (currentEntry.isComplete()) {
                            educationEntries.add(currentEntry)
                            currentEntry = null
                        }
                    }
                }
            }
        }
        
        // Add any remaining current entry
        currentEntry?.let { if (it.hasContent()) educationEntries.add(it) }
        
        // Sort by year (most recent first)
        val sortedEntries = educationEntries.sortedByDescending { it.getYearInt() }
        
        // Format the education entries
        val result = formatEducationEntries(sortedEntries)
        Log.d("DocumentExtractor", "Extracted education entries: ${educationEntries.size}")
        Log.d("DocumentExtractor", "Formatted education result: $result")
        return result
    }
    
    private fun extractSkillsDetails(lines: List<String>): String {
        val skillsKeywords = listOf("skills", "technical skills", "programming", "languages", "technologies", "tools", "competencies")
        return extractSection(lines, skillsKeywords)
    }
    
    private fun extractExperienceDetails(lines: List<String>): String {
        val experienceKeywords = listOf("experience", "work experience", "employment", "career", "job", "professional", "internship")
        return extractSection(lines, experienceKeywords)
    }
    
    private fun parseEducationLine(line: String): EducationEntry? {
        // Pattern: "B.Tech in CSE, SVCE" or "Bachelor of Technology, XYZ University, 2020"
        val patterns = listOf(
            Regex("([a-zA-Z.\\s]+)\\s+in\\s+([a-zA-Z\\s]+),\\s*([a-zA-Z\\s]+)(?:,\\s*(\\d{4}))?"),
            Regex("([a-zA-Z.\\s]+),\\s*([a-zA-Z\\s]+)(?:,\\s*(\\d{4}))?"),
            Regex("(\\d{4})\\s*-?\\s*(\\d{4})?\\s+([a-zA-Z.\\s]+)\\s*,?\\s*([a-zA-Z\\s]+)")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(line)
            if (match != null) {
                val groups = match.groupValues
                return when (groups.size) {
                    5 -> EducationEntry(
                        degree = groups[1].trim(),
                        institution = groups[3].trim(),
                        year = groups[4].ifEmpty { "" }
                    )
                    4 -> EducationEntry(
                        degree = groups[1].trim(),
                        institution = groups[2].trim(),
                        year = groups[3].ifEmpty { "" }
                    )
                    else -> null
                }
            }
        }
        
        return null
    }
    
    private fun extractDegreeFromLine(line: String, degreeKeywords: List<String>): String {
        val lowerLine = line.lowercase()
        for (keyword in degreeKeywords) {
            if (lowerLine.contains(keyword)) {
                // Extract the degree part
                val parts = line.split(",", "-", "•", "–")
                for (part in parts) {
                    if (part.lowercase().contains(keyword)) {
                        return part.trim()
                    }
                }
            }
        }
        return ""
    }
    
    private fun extractInstitutionFromLine(line: String, institutionKeywords: List<String>): String {
        val lowerLine = line.lowercase()
        
        // Look for institution keywords or common patterns
        if (institutionKeywords.any { lowerLine.contains(it) } || 
            lowerLine.matches(Regex(".*[a-z]+\\s+(university|college|institute|school).*"))) {
            
            val parts = line.split(",", "-", "•", "–")
            for (part in parts) {
                val partLower = part.lowercase()
                if (institutionKeywords.any { partLower.contains(it) } ||
                    partLower.matches(Regex(".*[a-z]+\\s+(university|college|institute|school).*"))) {
                    return part.trim()
                }
            }
        }
        
        return ""
    }
    
    private fun extractYearFromLine(line: String): String? {
        val yearPattern = Regex("(19|20)\\d{2}")
        val match = yearPattern.find(line)
        return match?.value
    }
    
    private fun isNewMajorSection(line: String): Boolean {
        val majorSections = listOf("experience", "skills", "projects", "certifications", "achievements", "contact", "objective")
        val lowerLine = line.lowercase()
        return majorSections.any { section -> 
            lowerLine == section || (lowerLine.contains(section) && line.length < 30)
        }
    }
    
    private fun formatEducationEntries(entries: List<EducationEntry>): String {
        if (entries.isEmpty()) return ""
        
        return entries.joinToString("\n\n") { entry ->
            val parts = mutableListOf<String>()
            
            if (entry.institution.isNotEmpty()) {
                parts.add(entry.institution)
            }
            
            if (entry.degree.isNotEmpty()) {
                parts.add(entry.degree)
            }
            
            if (entry.year.isNotEmpty()) {
                parts.add("(${entry.year})")
            }
            
            parts.joinToString(" – ")
        }
    }
    
    data class EducationEntry(
        var degree: String,
        var institution: String,
        var year: String
    ) {
        fun isComplete(): Boolean = degree.isNotEmpty() && institution.isNotEmpty()
        fun hasContent(): Boolean = degree.isNotEmpty() || institution.isNotEmpty()
        fun getYearInt(): Int = year.toIntOrNull() ?: 0
    }
}
