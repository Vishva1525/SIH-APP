package com.pmis.app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.InputStream
import java.util.regex.Pattern

data class ExtractedResumeInfo(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val currentLocation: String = "",
    val collegeName: String = "",
    val degree: String = "",
    val graduationYear: String = "",
    val cgpa: String = "",
    val stream: String = "",
    val technicalSkills: List<String> = emptyList(),
    val experience: String = "",
    val rawText: String = ""
)

class ResumeExtractor(private val context: Context) {
    
    suspend fun extractResumeData(uri: Uri): Result<ExtractedResumeInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d("ResumeExtractor", "Starting resume extraction from URI: $uri")
            
            // Extract raw text from PDF
            val rawText = extractTextFromPDF(uri)
            if (rawText.isBlank()) {
                return@withContext Result.failure(Exception("Unable to extract text from PDF. Please ensure the PDF contains selectable text."))
            }
            
            Log.d("ResumeExtractor", "Raw text extracted, length: ${rawText.length}")
            
            // Parse structured data from text
            val extractedInfo = parseResumeData(rawText)
            
            Log.d("ResumeExtractor", "Resume data extracted successfully")
            Result.success(extractedInfo)
            
        } catch (e: Exception) {
            Log.e("ResumeExtractor", "Error extracting resume data", e)
            Result.failure(e)
        }
    }
    
    private fun extractTextFromPDF(uri: Uri): String {
        var inputStream: InputStream? = null
        var document: PDDocument? = null
        
        return try {
            inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open input stream")
            
            document = PDDocument.load(inputStream)
            
            if (document.numberOfPages <= 0) {
                throw Exception("PDF has no pages")
            }
            
            val textStripper = PDFTextStripper().apply {
                setSortByPosition(true)
                setStartPage(1)
                setEndPage(document.numberOfPages)
            }
            
            val extractedText = textStripper.getText(document)
            Log.d("ResumeExtractor", "Text extracted successfully")
            extractedText
            
        } catch (e: Exception) {
            Log.e("ResumeExtractor", "Error extracting text from PDF", e)
            throw e
        } finally {
            try {
                document?.close()
                inputStream?.close()
            } catch (e: Exception) {
                Log.w("ResumeExtractor", "Error closing resources", e)
            }
        }
    }
    
    private fun parseResumeData(text: String): ExtractedResumeInfo {
        val cleanText = cleanText(text)
        
        return ExtractedResumeInfo(
            fullName = extractFullName(cleanText),
            email = extractEmail(cleanText),
            phoneNumber = extractPhoneNumber(cleanText),
            currentLocation = extractLocation(cleanText),
            collegeName = extractCollegeName(cleanText),
            degree = extractDegree(cleanText),
            graduationYear = extractGraduationYear(cleanText),
            cgpa = extractCGPA(cleanText),
            stream = extractStream(cleanText),
            technicalSkills = extractTechnicalSkills(cleanText),
            experience = extractExperience(cleanText),
            rawText = cleanText
        )
    }
    
    private fun cleanText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[\\x00-\\x1F\\x7F-\\x9F]"), " ")
            .trim()
    }
    
    private fun extractFullName(text: String): String {
        val lines = text.lines().take(10) // Check first 10 lines
        for (line in lines) {
            val cleanLine = line.trim()
            if (cleanLine.isNotEmpty() && cleanLine.length > 2 && cleanLine.length < 50) {
                // Check if line looks like a name (contains letters, spaces, no special chars)
                if (cleanLine.matches(Regex("^[a-zA-Z\\s.]+$")) && 
                    cleanLine.split(" ").size in 2..4 &&
                    !cleanLine.lowercase().contains("email") &&
                    !cleanLine.lowercase().contains("phone") &&
                    !cleanLine.lowercase().contains("address")) {
                    return cleanLine
                }
            }
        }
        return ""
    }
    
    private fun extractEmail(text: String): String {
        val emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")
        val matcher = emailPattern.matcher(text)
        return if (matcher.find()) matcher.group() else ""
    }
    
    private fun extractPhoneNumber(text: String): String {
        val phonePatterns = listOf(
            Pattern.compile("\\+?[0-9]{10,15}"),
            Pattern.compile("\\(?[0-9]{3}\\)?[-.\\s]?[0-9]{3}[-.\\s]?[0-9]{4}"),
            Pattern.compile("\\+91[\\s-]?[0-9]{10}")
        )
        
        for (pattern in phonePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val phone = matcher.group().replace(Regex("[^0-9+]"), "")
                if (phone.length >= 10) {
                    return phone
                }
            }
        }
        return ""
    }
    
    private fun extractLocation(text: String): String {
        val indianStates = listOf(
            "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
            "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
            "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
            "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
            "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
            "Uttar Pradesh", "Uttarakhand", "West Bengal",
            "Delhi", "Chandigarh", "Puducherry"
        )
        
        val lines = text.lines()
        for (line in lines) {
            val cleanLine = line.trim()
            for (state in indianStates) {
                if (cleanLine.contains(state, ignoreCase = true)) {
                    return state
                }
            }
        }
        return ""
    }
    
    private fun extractCollegeName(text: String): String {
        val educationKeywords = listOf("university", "college", "institute", "school", "academy")
        val lines = text.lines()
        
        for (line in lines) {
            val cleanLine = line.trim()
            if (educationKeywords.any { cleanLine.lowercase().contains(it) } &&
                cleanLine.length > 5 && cleanLine.length < 100) {
                return cleanLine
            }
        }
        return ""
    }
    
    private fun extractDegree(text: String): String {
        val degreePatterns = listOf(
            "Bachelor", "Master", "B.Tech", "M.Tech", "B.E", "M.E",
            "B.Sc", "M.Sc", "B.A", "M.A", "B.Com", "M.Com",
            "BBA", "MBA", "BCA", "MCA", "B.Pharm", "M.Pharm",
            "Diploma", "Certificate", "PhD", "Ph.D"
        )
        
        val lines = text.lines()
        for (line in lines) {
            val cleanLine = line.trim()
            for (pattern in degreePatterns) {
                if (cleanLine.contains(pattern, ignoreCase = true)) {
                    return cleanLine
                }
            }
        }
        return ""
    }
    
    private fun extractGraduationYear(text: String): String {
        val yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b")
        val matcher = yearPattern.matcher(text)
        val years = mutableListOf<String>()
        
        while (matcher.find()) {
            val year = matcher.group()
            if (year.toInt() in 1990..2030) {
                years.add(year)
            }
        }
        
        return years.maxOrNull() ?: ""
    }
    
    private fun extractCGPA(text: String): String {
        val cgpaPatterns = listOf(
            Pattern.compile("\\b\\d{1,2}\\.\\d{1,2}\\s*(?:/\\s*10)?\\b"),
            Pattern.compile("CGPA\\s*:?\\s*\\d{1,2}\\.\\d{1,2}"),
            Pattern.compile("GPA\\s*:?\\s*\\d{1,2}\\.\\d{1,2}")
        )
        
        for (pattern in cgpaPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val cgpa = matcher.group().replace(Regex("[^0-9.]"), "")
                val cgpaValue = cgpa.toDoubleOrNull()
                if (cgpaValue != null && cgpaValue in 0.0..10.0) {
                    return cgpa
                }
            }
        }
        return ""
    }
    
    private fun extractStream(text: String): String {
        val streamKeywords = listOf(
            "Computer Science", "Information Technology", "Electronics", "Electrical",
            "Mechanical", "Civil", "Chemical", "Aerospace", "Biotechnology",
            "Data Science", "Artificial Intelligence", "Machine Learning",
            "Software Engineering", "Computer Engineering", "IT", "CSE", "ECE", "EEE",
            "ME", "CE", "CHE", "AE", "BT", "AI", "ML", "DS"
        )
        
        val lines = text.lines()
        for (line in lines) {
            val cleanLine = line.trim()
            for (stream in streamKeywords) {
                if (cleanLine.contains(stream, ignoreCase = true)) {
                    return stream
                }
            }
        }
        return ""
    }
    
    private fun extractTechnicalSkills(text: String): List<String> {
        val commonSkills = listOf(
            "Python", "Java", "JavaScript", "React", "Angular", "Vue", "Node.js", "Express",
            "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "Docker", "Kubernetes",
            "AWS", "Azure", "GCP", "Git", "GitHub", "GitLab", "Jenkins", "CI/CD",
            "Android", "iOS", "Flutter", "React Native", "Kotlin", "Swift",
            "Machine Learning", "AI", "Data Science", "Analytics", "Tableau", "Power BI",
            "HTML", "CSS", "Bootstrap", "Sass", "Less", "Webpack", "Babel",
            "Spring", "Django", "Flask", "Laravel", "Rails", "ASP.NET", "PHP",
            "C++", "C#", "Go", "Rust", "Scala", "Ruby", "Perl", "R", "MATLAB",
            "TensorFlow", "PyTorch", "Keras", "Pandas", "NumPy", "Scikit-learn",
            "TypeScript", "jQuery", "Vue.js", "Next.js", "Nuxt.js", "Svelte"
        )
        
        val foundSkills = mutableSetOf<String>()
        val lowerText = text.lowercase()
        
        for (skill in commonSkills) {
            if (lowerText.contains(skill.lowercase())) {
                foundSkills.add(skill)
            }
        }
        
        return foundSkills.toList().take(10) // Limit to 10 skills
    }
    
    private fun extractExperience(text: String): String {
        val experienceKeywords = listOf(
            "experience", "work", "employment", "career", "professional", "internship",
            "job", "position", "role", "company", "organization", "employer",
            "project", "achievement", "responsibility", "duties", "accomplishment"
        )
        
        val lines = text.lines()
        val experienceLines = mutableListOf<String>()
        
        for (line in lines) {
            val cleanLine = line.trim()
            if (experienceKeywords.any { cleanLine.lowercase().contains(it) } &&
                cleanLine.length > 10 && cleanLine.length < 200) {
                experienceLines.add(cleanLine)
            }
        }
        
        return experienceLines.take(3).joinToString("\n")
    }
}
