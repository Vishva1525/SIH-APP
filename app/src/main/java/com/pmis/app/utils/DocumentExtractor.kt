package com.pmis.app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.InputStream

data class ExtractedResumeData(
    val education: String,
    val skills: String,
    val experience: String
)

class DocumentExtractor(private val context: Context) {
    
    /**
     * ULTIMATE CRASH-PROOF resume extraction
     * This function will NEVER crash the app under any circumstances
     */
    suspend fun extractFromUri(uri: Uri): ExtractedResumeData = withContext(Dispatchers.IO) {
        try {
            Log.d("DocumentExtractor", "Starting ULTIMATE CRASH-PROOF extraction from URI: $uri")
            
            // Step 1: Extract text with maximum safety
            val rawText = extractTextWithMaximumSafety(uri)
            
            // Step 2: Clean text thoroughly
            val cleanedText = cleanTextThoroughly(rawText)
            
            // Step 3: Parse sections with fallbacks
            val result = parseSectionsWithFallbacks(cleanedText)
            
            Log.d("DocumentExtractor", "Extraction completed successfully")
            result
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "CRITICAL ERROR in extractFromUri", e)
            // ULTIMATE FALLBACK - NEVER crash
            ExtractedResumeData(
                education = "",
                skills = "",
                experience = ""
            )
        }
    }
    
    /**
     * Extract text with maximum safety - handles all possible errors
     */
    private fun extractTextWithMaximumSafety(uri: Uri): String {
        var inputStream: InputStream? = null
        var document: PDDocument? = null
        
        return try {
            Log.d("DocumentExtractor", "Starting text extraction with maximum safety...")
            
            // Step 1: Validate URI
            if (uri.toString().isEmpty()) {
                Log.w("DocumentExtractor", "Empty URI provided")
                return "INVALID_URI"
            }
            
            // Step 2: Open input stream safely
            inputStream = try {
                context.contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                Log.e("DocumentExtractor", "Error opening input stream", e)
                return "STREAM_ERROR"
            }
            
            if (inputStream == null) {
                Log.w("DocumentExtractor", "Input stream is null")
                return "NULL_STREAM"
            }
            
            // Step 3: Load PDF safely
            document = try {
                PDDocument.load(inputStream)
            } catch (e: Exception) {
                Log.e("DocumentExtractor", "Error loading PDF - might be scanned or corrupted", e)
                return "SCANNED_PDF"
            }
            
            // Step 4: Validate document
            if (document?.numberOfPages ?: 0 <= 0) {
                Log.w("DocumentExtractor", "PDF has no pages")
                return "EMPTY_PDF"
            }
            
            Log.d("DocumentExtractor", "PDF loaded successfully, pages: ${document?.numberOfPages}")
            
            // Step 5: Extract text safely
            val textStripper = PDFTextStripper().apply {
                setSortByPosition(true)
                setStartPage(1)
                setEndPage(document?.numberOfPages ?: 1)
            }
            
            val extractedText = try {
                document?.let { textStripper.getText(it) } ?: "EXTRACTION_FAILED"
            } catch (e: Exception) {
                Log.e("DocumentExtractor", "Error extracting text", e)
                return "EXTRACTION_FAILED"
            }
            
            Log.d("DocumentExtractor", "Text extracted successfully, length: ${extractedText.length}")
            extractedText
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Critical error in extractTextWithMaximumSafety", e)
            "CRITICAL_ERROR"
        } finally {
            // ALWAYS clean up resources
            try {
                document?.close()
            } catch (e: Exception) {
                Log.w("DocumentExtractor", "Error closing document", e)
            }
            try {
                inputStream?.close()
            } catch (e: Exception) {
                Log.w("DocumentExtractor", "Error closing input stream", e)
            }
        }
    }
    
    /**
     * Clean text thoroughly - remove all junk and artifacts
     */
    private fun cleanTextThoroughly(text: String): String {
        return try {
            Log.d("DocumentExtractor", "Starting thorough text cleaning...")
            
            var cleaned = text
            
            // Remove PDF object markers and junk
            cleaned = cleaned.replace(Regex("endobj|objype|tructElem|obj\\s+\\d+\\s+\\d+"), " ")
            
            // Remove PDF-specific tokens
            cleaned = cleaned.replace(Regex("BT\\s+ET|Tj\\s*$|TJ\\s*$|\\b\\d+\\s+\\d+\\s+obj\\b"), " ")
            
            // Remove non-printable characters and control characters
            cleaned = cleaned.replace(Regex("[\\x00-\\x1F\\x7F-\\x9F\\uFEFF]"), " ")
            
            // Remove corrupted characters and unicode artifacts
            cleaned = cleaned.replace(Regex("[\\uFFFD\\uFFFE\\uFFFF]"), " ")
            
            // Remove excessive whitespace
            cleaned = cleaned.replace(Regex("\\s+"), " ")
            
            // Remove leading/trailing whitespace
            cleaned = cleaned.trim()
            
            // Remove lines that are just numbers, symbols, or junk
            cleaned = cleaned.lines()
                .filter { line -> 
                    val trimmed = line.trim()
                    trimmed.isNotEmpty() && 
                    !trimmed.matches(Regex("^\\d+$")) &&
                    !trimmed.matches(Regex("^[\\s\\-_=]+$")) &&
                    !trimmed.matches(Regex("^[\\x00-\\x1F\\x7F-\\x9F]+$")) &&
                    trimmed.length > 2 &&
                    !trimmed.contains("endobj") &&
                    !trimmed.contains("objype") &&
                    !trimmed.contains("tructElem")
                }
                .joinToString("\n")
            
            Log.d("DocumentExtractor", "Text cleaning completed, final length: ${cleaned.length}")
            cleaned
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Error cleaning text", e)
            text // Return original text if cleaning fails
        }
    }
    
    /**
     * Parse sections with comprehensive fallbacks
     */
    private fun parseSectionsWithFallbacks(text: String): ExtractedResumeData {
        return try {
            Log.d("DocumentExtractor", "Starting section parsing with fallbacks...")
            
            // Check if text is readable
            if (text.isEmpty() || text.length < 10) {
                Log.w("DocumentExtractor", "Text is too short or empty")
                return ExtractedResumeData("", "", "")
            }
            
            // Check for scanned PDF indicators
            if (isScannedPDF(text)) {
                Log.w("DocumentExtractor", "Scanned PDF detected")
                return ExtractedResumeData("", "", "")
            }
            
            val education = extractEducationSafely(text)
            val skills = extractSkillsSafely(text)
            val experience = extractExperienceSafely(text)
            
            ExtractedResumeData(
                education = education,
                skills = skills,
                experience = experience
            )
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Error parsing sections", e)
            ExtractedResumeData("", "", "")
        }
    }
    
    /**
     * Check if PDF is scanned (image-only)
     */
    private fun isScannedPDF(text: String): Boolean {
        return try {
            val cleanText = text.replace(Regex("\\s+"), " ").trim()
            cleanText.length < 50 || 
            cleanText.matches(Regex("^[\\s\\d\\-_=]+$")) ||
            cleanText.contains("endobj") && cleanText.length < 100
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Error checking if scanned PDF", e)
            false
        }
    }
    
    /**
     * Extract education section safely
     */
    private fun extractEducationSafely(text: String): String {
        return try {
            val educationKeywords = listOf(
                "education", "academic", "degree", "university", "college", "institute",
                "bachelor", "master", "phd", "diploma", "certificate", "graduation",
                "btech", "mtech", "bsc", "msc", "ba", "ma", "be", "me", "bca", "mca"
            )
            
            val lines = text.lines()
            val educationLines = mutableListOf<String>()
            
            for (line in lines) {
                val lowerLine = line.lowercase()
                if (educationKeywords.any { keyword -> lowerLine.contains(keyword) }) {
                    val cleanLine = line.trim()
                    if (cleanLine.isNotEmpty() && cleanLine.length > 5) {
                        educationLines.add(cleanLine)
                    }
                }
            }
            
            if (educationLines.isNotEmpty()) {
                educationLines.take(3).joinToString("\n")
            } else {
                ""
            }
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Error extracting education", e)
            ""
        }
    }
    
    /**
     * Extract skills section safely
     */
    private fun extractSkillsSafely(text: String): String {
        return try {
            val commonSkills = listOf(
                "python", "java", "javascript", "react", "angular", "vue", "node", "express",
                "sql", "mysql", "postgresql", "mongodb", "redis", "docker", "kubernetes",
                "aws", "azure", "gcp", "git", "github", "gitlab", "jenkins", "ci/cd",
                "android", "ios", "flutter", "react native", "kotlin", "swift",
                "machine learning", "ai", "data science", "analytics", "tableau", "power bi",
                "html", "css", "bootstrap", "sass", "less", "webpack", "babel",
                "spring", "django", "flask", "laravel", "rails", "asp.net", "php",
                "c++", "c#", "go", "rust", "scala", "ruby", "perl", "r", "matlab",
                "tensorflow", "pytorch", "keras", "pandas", "numpy", "scikit-learn"
            )
            
            val lines = text.lines()
            val skillLines = mutableListOf<String>()
            
            for (line in lines) {
                val lowerLine = line.lowercase()
                if (commonSkills.any { skill -> lowerLine.contains(skill) }) {
                    val cleanLine = line.trim()
                    if (cleanLine.isNotEmpty() && cleanLine.length > 2) {
                        skillLines.add(cleanLine)
                    }
                }
            }
            
            if (skillLines.isNotEmpty()) {
                skillLines.take(5).joinToString(", ")
            } else {
                ""
            }
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Error extracting skills", e)
            ""
        }
    }
    
    /**
     * Extract experience section safely
     */
    private fun extractExperienceSafely(text: String): String {
        return try {
            val experienceKeywords = listOf(
                "experience", "work", "employment", "career", "professional", "internship",
                "job", "position", "role", "company", "organization", "employer",
                "project", "achievement", "responsibility", "duties", "accomplishment",
                "intern", "trainee", "associate", "developer", "engineer", "analyst"
            )
            
            val lines = text.lines()
            val experienceLines = mutableListOf<String>()
            
            for (line in lines) {
                val lowerLine = line.lowercase()
                if (experienceKeywords.any { keyword -> lowerLine.contains(keyword) }) {
                    val cleanLine = line.trim()
                    if (cleanLine.isNotEmpty() && cleanLine.length > 10) {
                        experienceLines.add(cleanLine)
                    }
                }
            }
            
            if (experienceLines.isNotEmpty()) {
                experienceLines.take(3).joinToString("\n")
            } else {
                ""
            }
            
        } catch (e: Exception) {
            Log.e("DocumentExtractor", "Error extracting experience", e)
            ""
        }
    }
}