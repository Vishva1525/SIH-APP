package com.pmis.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.PurpleEnd
import com.pmis.app.ui.theme.CTAOrange
import com.pmis.app.data.MLRecommendationsApi
import com.pmis.app.utils.MLModelTest
import android.util.Log
import kotlinx.coroutines.launch

data class InternshipRecommendation(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val duration: String,
    val type: String,
    val matchScore: Int,
    val description: String,
    val requirements: List<String>,
    val benefits: List<String>,
    val skills: List<String>,
    val salary: String? = null,
    val isRemote: Boolean = false,
    val isUrgent: Boolean = false
)

data class FilterOption(
    val name: String,
    val isSelected: Boolean
)

@Composable
fun EnhancedRecommendationsScreen(
    internFormState: InternFormState,
    onBackClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(true) }
    var recommendations by remember { mutableStateOf<List<InternshipRecommendation>>(emptyList()) }
    var isUsingMLAPI by remember { mutableStateOf(false) }
    
    // Load recommendations from ML API
    LaunchedEffect(Unit) {
        try {
            Log.d("EnhancedRecommendations", "Starting ML API test...")
            
            // First test the API
            MLModelTest.testMLAPI()
            
            // Then get real recommendations
            val request = createRecommendationRequest(internFormState)
            Log.d("EnhancedRecommendations", "Request: $request")
            
            val result = MLRecommendationsApi.getRecommendations(request)
            result.fold(
                onSuccess = { response ->
                    Log.d("EnhancedRecommendations", "API Success: ${response.total_recommendations} recommendations")
                    recommendations = convertToInternshipRecommendations(response.recommendations)
                    isUsingMLAPI = true
                    isLoading = false
                },
                onFailure = { error ->
                    Log.e("EnhancedRecommendations", "API Failed: ${error.message}", error)
                    // Fallback to mock data if API fails
                    recommendations = generateMockRecommendations(internFormState)
                    isUsingMLAPI = false
                    isLoading = false
                }
            )
        } catch (e: Exception) {
            Log.e("EnhancedRecommendations", "Error getting recommendations", e)
            // Fallback to mock data
            recommendations = generateMockRecommendations(internFormState)
            isUsingMLAPI = false
            isLoading = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        RecommendationHeader(
            onBackClick = onBackClick,
            totalRecommendations = recommendations.size,
            isUsingMLAPI = isUsingMLAPI
        )
        
        if (isLoading) {
            LoadingSection()
        } else {
            // Filter Section
            FilterSection(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            
            // Recommendations List
            val filteredRecommendations = remember(recommendations, selectedFilter) {
                when (selectedFilter) {
                    "High Match" -> recommendations.filter { it.matchScore >= 80 }
                    "Remote" -> recommendations.filter { it.isRemote }
                    "Urgent" -> recommendations.filter { it.isUrgent }
                    "Recent" -> recommendations.sortedByDescending { it.id } // Mock recent sorting
                    else -> recommendations
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredRecommendations) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        onClick = { /* Handle click */ }
                    )
                }
                
                // Show empty state if no filtered results
                if (filteredRecommendations.isEmpty() && selectedFilter != "All") {
                    item {
                        EmptyFilterState(selectedFilter = selectedFilter)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationHeader(
    onBackClick: () -> Unit,
    totalRecommendations: Int,
    isUsingMLAPI: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PurpleStart,
                            PurpleEnd
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                Column {
                    Text(
                        text = "AI Recommendations",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                // Data source indicator
                Text(
                    text = if (isUsingMLAPI) "🤖 Powered by ML API" else "📊 Sample Data",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                if (isUsingMLAPI) {
                    Text(
                        text = "Using your skills, location, college tier, and CGPA",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                }
                    
                    // Test ML API Button
                    Button(
                        onClick = {
                            Log.d("EnhancedRecommendations", "Testing ML API manually...")
                            MLModelTest.testMLAPI()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CTAOrange,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Test ML", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Spacer(modifier = Modifier.width(40.dp)) // Balance the back button
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Found $totalRecommendations personalized recommendations for you",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = PurpleStart,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Analyzing your profile...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Finding the best matches for you",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All", "High Match", "Remote", "Urgent", "Recent")
    
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PurpleStart,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: InternshipRecommendation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with company and match score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recommendation.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = recommendation.company,
                        style = MaterialTheme.typography.titleMedium,
                        color = PurpleStart
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    MatchScoreChip(score = recommendation.matchScore)
                    if (recommendation.isUrgent) {
                        Spacer(modifier = Modifier.height(4.dp))
                        UrgentChip()
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Location and duration
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (recommendation.isRemote) "Remote" else recommendation.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = recommendation.duration,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = recommendation.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Skills
            Text(
                text = "Required Skills:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recommendation.skills.take(5)) { skill ->
                    SkillChip(skill = skill)
                }
                if (recommendation.skills.size > 5) {
                    item {
                        Text(
                            text = "+${recommendation.skills.size - 5} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Save for later */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PurpleStart
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
                
                Button(
                    onClick = { /* Apply now */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleStart,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply Now")
                }
            }
        }
    }
}

@Composable
private fun MatchScoreChip(score: Int) {
    val color = when {
        score >= 90 -> Color(0xFF4CAF50)
        score >= 70 -> CTAOrange
        else -> Color(0xFFFF9800)
    }
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$score% match",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = color
            )
        }
    }
}

@Composable
private fun UrgentChip() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5722).copy(alpha = 0.1f))
    ) {
        Text(
            text = "URGENT",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFFFF5722)
        )
    }
}

@Composable
private fun SkillChip(skill: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleStart.copy(alpha = 0.1f))
    ) {
        Text(
            text = skill,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = PurpleStart
        )
    }
}

@Composable
private fun EmptyFilterState(selectedFilter: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleStart.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = PurpleStart.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No $selectedFilter Internships Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try selecting a different filter or check back later for new opportunities.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Create a recommendation request from the intern form state
 */
private fun createRecommendationRequest(internFormState: InternFormState): MLRecommendationsApi.RecommendationRequest {
    val studentId = MLRecommendationsApi.generateStudentId()
    
    // Use new comprehensive input fields
    val skills = if (internFormState.technicalSkills.isNotEmpty()) {
        internFormState.technicalSkills
    } else {
        internFormState.skills // fallback to legacy field
    }
    
    val stream = if (internFormState.stream.isNotBlank()) {
        internFormState.stream
    } else {
        internFormState.preferredDomain.ifEmpty { "Computer Science" } // fallback
    }
    
    val cgpa = if (internFormState.cgpa.isNotBlank()) {
        internFormState.cgpa.toDoubleOrNull() ?: 8.0
    } else {
        internFormState.percentage.toDoubleOrNull() ?: 8.0 // fallback
    }
    
    val ruralUrban = if (internFormState.ruralUrbanClassification.isNotBlank()) {
        internFormState.ruralUrbanClassification
    } else {
        MLRecommendationsApi.mapLocationToRuralUrban(internFormState.currentLocation.ifEmpty { internFormState.location })
    }
    
    val collegeTier = if (internFormState.collegeTier.isNotBlank()) {
        internFormState.collegeTier
    } else {
        MLRecommendationsApi.mapCollegeToTier(internFormState.collegeName)
    }
    
    // Log all inputs being sent to ML model
    Log.d("ML_Inputs", "=== COMPREHENSIVE ML MODEL INPUTS ===")
    Log.d("ML_Inputs", "Student ID: $studentId")
    Log.d("ML_Inputs", "Name: ${internFormState.fullName}")
    Log.d("ML_Inputs", "Email: ${internFormState.email}")
    Log.d("ML_Inputs", "Phone: ${internFormState.phoneNumber}")
    Log.d("ML_Inputs", "Technical Skills: $skills")
    Log.d("ML_Inputs", "Career Interests: ${internFormState.careerInterests}")
    Log.d("ML_Inputs", "Domain Interests: ${internFormState.domainInterests}")
    Log.d("ML_Inputs", "Stream: $stream")
    Log.d("ML_Inputs", "CGPA: $cgpa")
    Log.d("ML_Inputs", "Current Location: ${internFormState.currentLocation}")
    Log.d("ML_Inputs", "Preferred Work Locations: ${internFormState.preferredWorkLocation}")
    Log.d("ML_Inputs", "Rural/Urban: $ruralUrban")
    Log.d("ML_Inputs", "College: ${internFormState.collegeName}")
    Log.d("ML_Inputs", "College Tier: $collegeTier")
    Log.d("ML_Inputs", "Internship Duration: ${internFormState.internshipDuration}")
    Log.d("ML_Inputs", "Stipend Expectations: ${internFormState.stipendExpectations}")
    // Optional enhancement fields removed
    Log.d("ML_Inputs", "Optional enhancement details: Not collected")
    Log.d("ML_Inputs", "=====================================")
    
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
 * Convert API recommendations to UI recommendations
 */
private fun convertToInternshipRecommendations(apiRecommendations: List<MLRecommendationsApi.Recommendation>): List<InternshipRecommendation> {
    Log.d("ML_Outputs", "=== ML MODEL OUTPUTS ===")
    Log.d("ML_Outputs", "Total recommendations received: ${apiRecommendations.size}")
    
    val convertedRecommendations = apiRecommendations.mapIndexed { index, rec ->
        Log.d("ML_Outputs", "Recommendation ${index + 1}:")
        Log.d("ML_Outputs", "  - Title: ${rec.title}")
        Log.d("ML_Outputs", "  - Company: ${rec.organization_name}")
        Log.d("ML_Outputs", "  - Location: ${rec.location}")
        Log.d("ML_Outputs", "  - Success Probability: ${rec.success_prob}")
        Log.d("ML_Outputs", "  - Stipend: ₹${rec.stipend}")
        Log.d("ML_Outputs", "  - Domain: ${rec.domain}")
        Log.d("ML_Outputs", "  - Missing Skills: ${rec.missing_skills}")
        Log.d("ML_Outputs", "  - Reasons: ${rec.reasons}")
        
        InternshipRecommendation(
            id = rec.internship_id,
            title = rec.title,
            company = rec.organization_name,
            location = rec.location,
            duration = rec.duration,
            type = "Full-time", // Default type
            matchScore = (rec.success_prob * 100).toInt(),
            description = "Based on your profile: ${rec.reasons.joinToString(", ")}",
            requirements = rec.missing_skills,
            benefits = listOf("Mentorship", "Certificate", "Real-world experience"),
            skills = rec.missing_skills,
            salary = "₹${rec.stipend.toInt()}/month",
            isRemote = rec.location.lowercase().contains("remote"),
            isUrgent = rec.success_prob > 0.8
        )
    }
    
    Log.d("ML_Outputs", "========================")
    return convertedRecommendations
}

private fun generateMockRecommendations(@Suppress("UNUSED_PARAMETER") internFormState: InternFormState): List<InternshipRecommendation> {
    return listOf(
        InternshipRecommendation(
            id = "1",
            title = "Software Development Intern",
            company = "TechCorp Solutions",
            location = "Bangalore",
            duration = "6 months",
            type = "Full-time",
            matchScore = 95,
            description = "Join our dynamic team to work on cutting-edge web applications using React, Node.js, and cloud technologies.",
            requirements = listOf("React", "JavaScript", "Node.js", "Git"),
            benefits = listOf("Mentorship", "Stipend", "Certificate", "Job opportunity"),
            skills = listOf("React", "JavaScript", "Node.js", "MongoDB", "Git", "AWS"),
            salary = "₹15,000/month",
            isRemote = false,
            isUrgent = true
        ),
        InternshipRecommendation(
            id = "2",
            title = "Data Science Intern",
            company = "DataInsights Pvt Ltd",
            location = "Mumbai",
            duration = "3 months",
            type = "Part-time",
            matchScore = 88,
            description = "Work on real-world data analysis projects using Python, machine learning, and statistical modeling.",
            requirements = listOf("Python", "Pandas", "Scikit-learn", "SQL"),
            benefits = listOf("Real projects", "Learning resources", "Certificate"),
            skills = listOf("Python", "Pandas", "Scikit-learn", "SQL", "Jupyter", "Statistics"),
            salary = "₹12,000/month",
            isRemote = true,
            isUrgent = false
        ),
        InternshipRecommendation(
            id = "3",
            title = "UI/UX Design Intern",
            company = "Creative Studio",
            location = "Delhi",
            duration = "4 months",
            type = "Full-time",
            matchScore = 82,
            description = "Design user interfaces and experiences for mobile and web applications using modern design tools.",
            requirements = listOf("Figma", "Adobe XD", "Design thinking", "Prototyping"),
            benefits = listOf("Portfolio building", "Design mentorship", "Certificate"),
            skills = listOf("Figma", "Adobe XD", "Sketch", "Prototyping", "User Research", "Wireframing"),
            salary = "₹10,000/month",
            isRemote = false,
            isUrgent = false
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun EnhancedRecommendationsScreenPreview() {
    PMISAppTheme {
        EnhancedRecommendationsScreen(
            internFormState = InternFormState()
        )
    }
}

