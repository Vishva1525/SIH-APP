package com.pmis.app.utils

import com.pmis.app.data.MLRecommendationsApi
import com.pmis.app.screens.InternFormState

/**
 * Utility class to extract and format intern registration data
 * for the ML Recommendations API
 */
object InternDataExtractor {
    
    /**
     * Extract student data from intern registration form for ML API
     */
    fun extractStudentData(formState: InternFormState): MLRecommendationsApi.RecommendationRequest {
        val studentId = MLRecommendationsApi.generateStudentId()
        
        // Extract skills from the form
        val skills = extractSkills(formState)
        
        // Map academic stream
        val stream = mapAcademicStream(formState.extractedEducation)
        
        // Extract CGPA (convert percentage to CGPA if needed)
        val cgpa = extractCGPA(formState.percentage)
        
        // Determine location type
        val ruralUrban = MLRecommendationsApi.mapLocationToRuralUrban(formState.currentLocation)
        
        // Map college to tier
        val collegeTier = MLRecommendationsApi.mapCollegeToTier(formState.collegeName)
        
        return MLRecommendationsApi.RecommendationRequest(
            student_id = studentId,
            skills = skills,
            stream = stream,
            cgpa = cgpa,
            rural_urban = ruralUrban,
            college_tier = collegeTier
        )
    }
    
    /**
     * Extract skills from various form fields
     */
    private fun extractSkills(formState: InternFormState): List<String> {
        val skills = mutableListOf<String>()
        
        // Add skills from skills section
        if (formState.extractedSkills.isNotEmpty()) {
            val skillLines = formState.extractedSkills.split("\n")
            skillLines.forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty()) {
                    // Split by common delimiters and clean up
                    val individualSkills = trimmedLine.split(",", ";", "|", "-")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && it.length > 2 }
                        .filter { !it.matches(Regex("^\\d+$")) } // Remove pure numbers
                    
                    skills.addAll(individualSkills)
                }
            }
        }
        
        // Add technical skills from experience section
        if (formState.extractedExperience.isNotEmpty()) {
            val techSkills = extractTechnicalSkillsFromExperience(formState.extractedExperience)
            skills.addAll(techSkills)
        }
        
        // Add programming languages and tools if mentioned in education
        if (formState.extractedEducation.isNotEmpty()) {
            val eduSkills = extractSkillsFromEducation(formState.extractedEducation)
            skills.addAll(eduSkills)
        }
        
        // Remove duplicates and clean up
        return skills.distinct()
            .map { cleanSkillName(it) }
            .filter { it.isNotEmpty() && it.length > 2 }
            .take(20) // Limit to top 20 skills
    }
    
    /**
     * Extract technical skills from experience section
     */
    private fun extractTechnicalSkillsFromExperience(experience: String): List<String> {
        val techKeywords = listOf(
            "python", "java", "javascript", "react", "angular", "vue", "node", "express",
            "sql", "mysql", "postgresql", "mongodb", "firebase", "aws", "azure", "gcp",
            "docker", "kubernetes", "git", "github", "gitlab", "jenkins", "ci/cd",
            "machine learning", "ml", "ai", "artificial intelligence", "data science",
            "android", "ios", "flutter", "react native", "kotlin", "swift",
            "html", "css", "bootstrap", "tailwind", "sass", "less",
            "tensorflow", "pytorch", "scikit-learn", "pandas", "numpy", "matplotlib",
            "spring", "django", "flask", "fastapi", "express", "laravel", "rails",
            "linux", "ubuntu", "centos", "windows", "macos", "unix",
            "agile", "scrum", "kanban", "devops", "microservices", "api", "rest", "graphql"
        )
        
        val experienceLower = experience.lowercase()
        return techKeywords.filter { keyword ->
            experienceLower.contains(keyword)
        }
    }
    
    /**
     * Extract skills from education section
     */
    private fun extractSkillsFromEducation(education: String): List<String> {
        val eduKeywords = listOf(
            "computer science", "cs", "information technology", "it", "software engineering",
            "data science", "machine learning", "artificial intelligence", "ai",
            "cyber security", "network security", "web development", "mobile development",
            "database", "cloud computing", "blockchain", "iot", "embedded systems"
        )
        
        val educationLower = education.lowercase()
        return eduKeywords.filter { keyword ->
            educationLower.contains(keyword)
        }
    }
    
    /**
     * Clean and standardize skill names
     */
    private fun cleanSkillName(skill: String): String {
        return skill.trim()
            .lowercase()
            .replace(Regex("[^a-zA-Z0-9\\s]"), "") // Remove special characters
            .replace(Regex("\\s+"), " ") // Normalize spaces
            .trim()
    }
    
    /**
     * Map degree to academic stream
     */
    private fun mapAcademicStream(degree: String): String {
        val degreeLower = degree.lowercase()
        return when {
            degreeLower.contains("computer") || degreeLower.contains("cs") || degreeLower.contains("it") -> "Computer Science"
            degreeLower.contains("electronics") || degreeLower.contains("ece") -> "Electronics"
            degreeLower.contains("mechanical") || degreeLower.contains("me") -> "Mechanical"
            degreeLower.contains("civil") || degreeLower.contains("ce") -> "Civil"
            degreeLower.contains("electrical") || degreeLower.contains("ee") -> "Electrical"
            degreeLower.contains("chemical") || degreeLower.contains("ch") -> "Chemical"
            degreeLower.contains("biotechnology") || degreeLower.contains("biotech") -> "Biotechnology"
            degreeLower.contains("business") || degreeLower.contains("mba") -> "Business"
            degreeLower.contains("commerce") || degreeLower.contains("bcom") -> "Commerce"
            degreeLower.contains("arts") || degreeLower.contains("ba") -> "Arts"
            degreeLower.contains("science") || degreeLower.contains("bsc") -> "Science"
            else -> "Engineering" // Default to Engineering
        }
    }
    
    /**
     * Extract and convert CGPA from percentage or direct input
     */
    private fun extractCGPA(percentage: String): Double {
        return try {
            val percentageValue = percentage.replace("%", "").trim().toDoubleOrNull() ?: 0.0
            
            // Convert percentage to CGPA (assuming 10-point scale)
            // Common conversion: CGPA = (Percentage - 50) / 5
            // But we'll use a more standard conversion
            when {
                percentageValue >= 90 -> 9.0
                percentageValue >= 80 -> 8.0
                percentageValue >= 70 -> 7.0
                percentageValue >= 60 -> 6.0
                percentageValue >= 50 -> 5.0
                percentageValue > 0 -> percentageValue / 10.0 // Direct conversion
                else -> 7.0 // Default CGPA
            }
        } catch (e: Exception) {
            7.0 // Default CGPA if parsing fails
        }
    }
    
    /**
     * Validate if we have enough data for ML recommendations
     */
    fun hasEnoughData(formState: InternFormState): Boolean {
        return formState.fullName.isNotEmpty() &&
               formState.extractedEducation.isNotEmpty() &&
               (formState.extractedSkills.isNotEmpty() || formState.extractedExperience.isNotEmpty())
    }
    
    /**
     * Get missing required fields for ML recommendations
     */
    fun getMissingFields(formState: InternFormState): List<String> {
        val missing = mutableListOf<String>()
        
        if (formState.fullName.isEmpty()) missing.add("Full Name")
        if (formState.extractedEducation.isEmpty()) missing.add("Education")
        if (formState.extractedSkills.isEmpty() && formState.extractedExperience.isEmpty()) {
            missing.add("Skills or Experience")
        }
        
        return missing
    }
}
