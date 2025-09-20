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
                education = cleanAndFormatContent(education).ifEmpty { "No education information found. Please add manually." },
                skills = cleanAndFormatContent(skills).ifEmpty { "No skills information found. Please add manually." },
                experience = cleanAndFormatContent(experience).ifEmpty { "No experience information found. Please add manually." }
            )
        }
    
    private fun extractSection(lines: List<String>, keywords: List<String>): String {
        val result = StringBuilder()
        var inSection = false
        var sectionDepth = 0
        
        for (line in lines) {
            val cleanLineText = cleanLine(line)
            if (cleanLineText.isEmpty()) continue
            
            val lowerLine = cleanLineText.lowercase()
            
            // Check if this line starts a new section
            val startsNewSection = keywords.any { keyword ->
                lowerLine.contains(keyword) || lowerLine.startsWith(keyword)
            }
            
            if (startsNewSection) {
                inSection = true
                sectionDepth = 0
                // Don't include the section header itself
                continue
            }
            
            // If we're in a section, collect content
            if (inSection) {
                // Check if this might be the start of a different section
                val isOtherSection = cleanLineText.isNotEmpty() && 
                    !cleanLineText.startsWith(" ") && 
                    !cleanLineText.startsWith("-") && 
                    !cleanLineText.startsWith("•") &&
                    !cleanLineText.matches(Regex(".*\\d{4}.*")) && // Not a date
                    !cleanLineText.matches(Regex(".*@.*")) && // Not an email
                    !cleanLineText.matches(Regex(".*\\d{10}.*")) && // Not a phone number
                    isNewMajorSection(cleanLineText) // Check for new major sections
                
                if (isOtherSection && sectionDepth > 2) {
                    // Probably a new section, stop here
                    break
                }
                
                // Only add meaningful content
                if (isMeaningfulContent(cleanLineText)) {
                    result.append("$cleanLineText\n")
                    sectionDepth++
                }
                
                // Stop after collecting reasonable amount of content
                if (sectionDepth > 15) {
                    break
                }
            }
        }
        
        return result.toString().trim()
    }
    
        private fun isMeaningfulContent(line: String): Boolean {
            val trimmed = line.trim()
            return trimmed.length > 4 && // At least 5 characters
                   !trimmed.matches(Regex("^[\\d\\s\\-\\.,;:!?()]+$")) && // Not just punctuation/numbers
                   !trimmed.matches(Regex("^\\s*[a-z]\\s*$")) && // Not single letters
                   !trimmed.matches(Regex("^\\s*[A-Z]{1,2}\\s*$")) && // Not single/double uppercase letters like "E", "A"
                   !trimmed.matches(Regex("^\\s*[/\\\\][A-Za-z]\\s*$")) && // Not /E, /A, \B patterns
                   !trimmed.lowercase().matches(Regex("^(page|section|chapter|footer|header)\\s*\\d*$")) && // Not page markers
                   !trimmed.matches(Regex("^\\s*[\\-_=]{3,}\\s*$")) && // Not separator lines
                   !trimmed.matches(Regex("^\\s*\\d+\\s*$")) && // Not just numbers
                   !trimmed.matches(Regex("^\\s*[\\^\\`\\~]\\s*$")) && // Not single special chars
                   !trimmed.matches(Regex("^[?\\s]+$")) && // Not just question marks and spaces
                   !trimmed.matches(Regex("^[^a-zA-Z]*$")) && // Must contain at least one letter
                   trimmed.contains(Regex("[a-zA-Z]{3,}")) && // Must contain meaningful words (3+ letters)
                   !trimmed.contains(Regex("[?]{3,}")) // Not lines with many question marks
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
            val cleanLineText = cleanLine(line)
            if (cleanLineText.isEmpty()) continue
            
            val lowerLine = cleanLineText.lowercase()
            
            // Check if we're entering education section
            if (educationKeywords.any { keyword -> lowerLine.contains(keyword) && lowerLine.length < 50 }) {
                inEducationSection = true
                continue
            }
            
            // Check if we're leaving education section (new major section)
            if (inEducationSection && isNewMajorSection(cleanLineText)) {
                break
            }
            
            if (inEducationSection) {
                // Only process meaningful content
                if (!isMeaningfulContent(cleanLineText)) continue
                
                // Try to extract education information from this line
                val extractedEntry = parseEducationLine(cleanLineText)
                if (extractedEntry != null) {
                    educationEntries.add(extractedEntry)
                } else {
                    // Check if this line contains degree or institution info
                    val degree = extractDegreeFromLine(cleanLineText, degreeKeywords)
                    val institution = extractInstitutionFromLine(cleanLineText, institutionKeywords)
                    
                    if (degree.isNotEmpty() || institution.isNotEmpty()) {
                        if (currentEntry == null) {
                            currentEntry = EducationEntry("", "", "")
                        }
                        if (degree.isNotEmpty()) currentEntry.degree = degree
                        if (institution.isNotEmpty()) currentEntry.institution = institution
                        
                        // Try to extract year from this line or next lines
                        val year = extractYearFromLine(cleanLineText) ?: 
                                  if (i < lines.size - 1) extractYearFromLine(cleanLine(lines[i + 1])) else null
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
                        degree = cleanLine(groups[1].trim()),
                        institution = cleanLine(groups[3].trim()),
                        year = groups[4].ifEmpty { "" }
                    )
                    4 -> EducationEntry(
                        degree = cleanLine(groups[1].trim()),
                        institution = cleanLine(groups[2].trim()),
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
                        return cleanLine(part.trim())
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
                    return cleanLine(part.trim())
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
    
        private fun cleanAndFormatContent(content: String): String {
            if (content.isEmpty()) return content
            
            val cleaned = cleanContent(content)
            
            // Additional formatting for better readability
            return cleaned
                .split("\n")
                .map { line -> 
                    line.trim()
                        .replace(Regex("\\s+"), " ") // Normalize spaces
                        .replace(Regex("^[\\-\\*\\+]\\s*"), "") // Remove bullet points
                        .replace(Regex("^\\d+\\.\\s*"), "") // Remove numbered lists
                        .trim()
                }
                .filter { line -> 
                    line.length > 3 && 
                    !line.matches(Regex("^[\\d\\s\\-\\.,;:!?()]+$")) &&
                    line.contains(Regex("[a-zA-Z]{3,}"))
                }
                .distinct() // Remove duplicate lines
                .joinToString("\n")
                .trim()
        }
        
        private fun cleanContent(content: String): String {
            if (content.isEmpty()) return content
            
            val originalLength = content.length
            val cleaned = content
                // Remove escape characters and control characters
                .replace(Regex("[\\x00-\\x1F\\x7F]"), "")
                .replace("\\e", "")
                .replace("/E", "") // Remove /E artifacts
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\r", "")
                
                // Remove common OCR artifacts and meaningless patterns
                .replace(Regex("\\blang\\b"), "") // Remove "lang" words
                .replace(Regex("\\b\\d{1,2}\\b(?![0-9]{2,})"), "") // Remove single/double digits that aren't years
                .replace(Regex("^\\s*[\\d\\s]+$"), "") // Remove lines that are only numbers and spaces
                .replace(Regex("[/\\\\][A-Za-z]"), "") // Remove /E, /A, \B, etc. patterns
                .replace(Regex("[\\^\\`\\~]"), "") // Remove caret, backtick, tilde
                .replace(Regex("\\b[A-Z]{1,2}\\b(?![A-Z]{3,})"), "") // Remove single/double letter words like "E", "A", "B"
                
                // Remove common meaningless symbols and patterns
                .replace(Regex("[\\[\\]{}|~`]"), "")
                .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
                
                // Remove garbled text patterns and unwanted symbols
                .replace(Regex("[?]{2,}"), "") // Remove multiple question marks
                .replace(Regex("[^\\w\\s\\-\\.,;:!()@\\+]"), " ") // Keep only meaningful characters
                .replace(Regex("\\s+"), " ") // Normalize spaces again
                
                // Remove lines that are too short or meaningless
                .split("\n")
                .filter { line ->
                    val trimmed = line.trim()
                    trimmed.length > 3 && // At least 4 characters
                    !trimmed.matches(Regex("^[\\d\\s\\-\\.,;:!?()]+$")) && // Not just punctuation/numbers
                    !trimmed.matches(Regex("^\\s*[a-z]\\s*$")) && // Not single letters
                    !trimmed.lowercase().matches(Regex("^(page|section|chapter)\\s*\\d*$")) && // Not page/section markers
                    !trimmed.matches(Regex("^\\s*[\\-_=]{3,}\\s*$")) && // Not separator lines
                    !trimmed.matches(Regex("^[?\\s]+$")) && // Not just question marks and spaces
                    !trimmed.matches(Regex("^[^a-zA-Z]*$")) && // Must contain at least one letter
                    trimmed.contains(Regex("[a-zA-Z]{3,}")) // Must contain meaningful words
                }
                .map { line -> cleanLine(line) } // Clean each line individually
                .filter { it.isNotEmpty() } // Remove empty lines
                .joinToString("\n")
                .trim()
            
            Log.d("DocumentExtractor", "Content cleaned: $originalLength -> ${cleaned.length} characters")
            return cleaned
        }
    
        private fun cleanLine(line: String): String {
            return line
                .replace(Regex("[\\x00-\\x1F\\x7F]"), "") // Remove control characters
                .replace(Regex("[?]{2,}"), "") // Remove multiple question marks
                .replace(Regex("[^\\w\\s\\-\\.,;:!()@\\+]"), " ") // Keep only meaningful characters
                .replace(Regex("\\s+"), " ") // Normalize whitespace
                .replace(Regex("^\\s*[\\d\\s\\-\\.,;:!?()]+$"), "") // Remove lines with only punctuation
                .trim()
        }
}
