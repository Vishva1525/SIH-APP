package com.pmis.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.data.MLRecommendationsApi
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.utils.InternDataExtractor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    internFormState: InternFormState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Debug logging
    LaunchedEffect(Unit) {
        android.util.Log.d("RecommendationsScreen", "RecommendationsScreen loaded with form data:")
        android.util.Log.d("RecommendationsScreen", "fullName: ${internFormState.fullName}")
        android.util.Log.d("RecommendationsScreen", "extractedEducation: ${internFormState.extractedEducation}")
        android.util.Log.d("RecommendationsScreen", "extractedSkills: ${internFormState.extractedSkills}")
        android.util.Log.d("RecommendationsScreen", "extractedExperience: ${internFormState.extractedExperience}")
    }
    var isLoading by remember { mutableStateOf(true) }
    var recommendations by remember { mutableStateOf<MLRecommendationsApi.RecommendationResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isApiHealthy by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Check API health and load recommendations
    LaunchedEffect(Unit) {
        android.util.Log.d("RecommendationsScreen", "Starting ML API health check...")
        
        // Check API health first
        isApiHealthy = MLRecommendationsApi.checkHealth()
        android.util.Log.d("RecommendationsScreen", "API health check result: $isApiHealthy")
        
        if (isApiHealthy) {
            // Validate if we have enough data
            val hasEnoughData = InternDataExtractor.hasEnoughData(internFormState)
            android.util.Log.d("RecommendationsScreen", "Has enough data: $hasEnoughData")
            
            if (hasEnoughData) {
                // Extract student data
                android.util.Log.d("RecommendationsScreen", "Extracting student data...")
                val studentData = InternDataExtractor.extractStudentData(internFormState)
                android.util.Log.d("RecommendationsScreen", "Student data extracted: $studentData")
                
                // Get recommendations from ML API
                android.util.Log.d("RecommendationsScreen", "Calling ML API for recommendations...")
                val result = MLRecommendationsApi.getRecommendations(studentData)
                
                result.fold(
                    onSuccess = { response ->
                        android.util.Log.d("RecommendationsScreen", "ML API success! Got ${response.total_recommendations} recommendations")
                        recommendations = response
                        isLoading = false
                    },
                    onFailure = { exception ->
                        android.util.Log.e("RecommendationsScreen", "ML API failed: ${exception.message}", exception)
                        errorMessage = "Failed to get recommendations: ${exception.message}"
                        isLoading = false
                    }
                )
            } else {
                val missingFields = InternDataExtractor.getMissingFields(internFormState)
                android.util.Log.w("RecommendationsScreen", "Missing fields: $missingFields")
                errorMessage = "Please complete the following fields first: ${missingFields.joinToString(", ")}"
                isLoading = false
            }
        } else {
            android.util.Log.w("RecommendationsScreen", "ML API is unhealthy")
            errorMessage = "ML Recommendations service is currently unavailable. Please try again later."
            isLoading = false
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "AI Recommendations",
                    fontWeight = FontWeight.Bold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
        // Main Content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    LoadingView()
                }
                
                errorMessage != null -> {
                    ErrorView(
                        message = errorMessage!!,
                        isApiHealthy = isApiHealthy,
                        onRetry = {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                // Retry logic
                                val studentData = InternDataExtractor.extractStudentData(internFormState)
                                val result = MLRecommendationsApi.getRecommendations(studentData)
                                
                                result.fold(
                                    onSuccess = { response ->
                                        recommendations = response
                                        isLoading = false
                                    },
                                    onFailure = { exception ->
                                        errorMessage = "Failed to get recommendations: ${exception.message}"
                                        isLoading = false
                                    }
                                )
                            }
                        }
                    )
                }
                
                recommendations != null -> {
                    RecommendationsList(
                        recommendations = recommendations!!,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
        
        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Getting AI-powered recommendations...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This may take a few moments",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    isApiHealthy: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isApiHealthy) Icons.Default.Warning else Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isApiHealthy) "Recommendation Error" else "Service Unavailable",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        if (isApiHealthy) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun RecommendationsList(
    recommendations: MLRecommendationsApi.RecommendationResponse,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🎯 AI-Powered Recommendations",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Found ${recommendations.total_recommendations} personalized internship opportunities",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Generated at: ${recommendations.generated_at}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        // Recommendations
        items(recommendations.recommendations) { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onCourseClick = { course ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Opening: ${course.name}")
                        // TODO: Open course URL in browser
                    }
                }
            )
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: MLRecommendationsApi.Recommendation,
    onCourseClick: (MLRecommendationsApi.CourseInfo) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Success Probability Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // Success Probability
                Surface(
                    color = when {
                        recommendation.success_prob >= 0.8 -> Color(0xFF4CAF50)
                        recommendation.success_prob >= 0.6 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${(recommendation.success_prob * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Organization and Domain
            Text(
                text = "${recommendation.organization_name} • ${recommendation.domain}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Location and Duration
            Text(
                text = "${recommendation.location} • ${recommendation.duration}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Stipend
            Text(
                text = "₹${recommendation.stipend.toInt()}/month",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Missing Skills
            if (recommendation.missing_skills.isNotEmpty()) {
                Text(
                    text = "Skills to develop:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(recommendation.missing_skills.take(5)) { skill ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Recommended Courses
            if (recommendation.courses.isNotEmpty()) {
                Text(
                    text = "Recommended Courses:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                recommendation.courses.take(3).forEach { course ->
                    CourseItem(
                        course = course,
                        onClick = { onCourseClick(course) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Reasons
            if (recommendation.reasons.isNotEmpty()) {
                Text(
                    text = "Why this internship?",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                recommendation.reasons.take(2).forEach { reason ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseItem(
    course: MLRecommendationsApi.CourseInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = course.platform,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open course",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendationsScreenPreview() {
    PMISAppTheme {
        RecommendationsScreen(
            internFormState = InternFormState().apply {
                fullName = "John Doe"
                extractedEducation = "Computer Science"
                extractedSkills = "Python, Machine Learning, SQL"
                extractedExperience = "Software Development Intern at XYZ Corp"
                percentage = "85%"
            },
            onBackClick = {}
        )
    }
}
