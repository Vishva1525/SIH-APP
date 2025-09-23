package com.pmis.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pmis.app.components.InternshipCard
import com.pmis.app.components.InternshipDetailsPopup
import com.pmis.app.data.InternshipRecommendation
import com.pmis.app.data.CourseSuggestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipPopupDemo() {
    var selectedInternship by remember { mutableStateOf<InternshipRecommendation?>(null) }
    var comparingInternships by remember { mutableStateOf(setOf<String>()) }
    
    val sampleInternships = remember {
        listOf(
            InternshipRecommendation(
                id = "intern_001",
                title = "Software Development Intern",
                company = "TechCorp India",
                location = "Bangalore, Karnataka",
                duration = "6 months",
                type = "Technology",
                matchScore = 85,
                description = "Join our dynamic team as a Software Development Intern and work on cutting-edge projects.",
                requirements = listOf("Java", "Python", "Git", "Problem Solving"),
                benefits = listOf("Mentorship", "Certificate", "Stipend", "Flexible Hours"),
                skills = listOf("Java", "Python", "React", "SQL", "Git", "Docker"),
                salary = "25000",
                missing_skills = listOf("React Native", "AWS", "Docker", "Kubernetes"),
                course_suggestions = listOf(
                    CourseSuggestion(
                        name = "React Native Development",
                        platform = "Coursera",
                        duration_estimate = "4 weeks",
                        description = "Learn to build cross-platform mobile apps with React Native",
                        enrollment_url = "https://coursera.org/learn/react-native"
                    ),
                    CourseSuggestion(
                        name = "AWS Fundamentals",
                        platform = "Udemy",
                        duration_estimate = "6 weeks",
                        description = "Master cloud computing with Amazon Web Services",
                        enrollment_url = "https://udemy.com/aws-fundamentals"
                    )
                ),
                reasons = listOf(
                    "Strong match with your Java and Python skills",
                    "Location preference aligns with your profile",
                    "Duration fits your academic schedule perfectly"
                ),
                application_deadline = "2024-03-15",
                rank = 1
            ),
            InternshipRecommendation(
                id = "intern_002",
                title = "Data Science Intern",
                company = "Analytics Pro",
                location = "Mumbai, Maharashtra",
                duration = "4 months",
                type = "Data Science",
                matchScore = 72,
                description = "Work on cutting-edge data science projects with real-world datasets.",
                requirements = listOf("Python", "Statistics", "Machine Learning", "Data Analysis"),
                benefits = listOf("Research Experience", "Certificate", "Stipend", "Networking"),
                skills = listOf("Python", "Pandas", "NumPy", "Matplotlib", "Scikit-learn"),
                salary = "30000",
                missing_skills = listOf("TensorFlow", "PyTorch", "Apache Spark"),
                course_suggestions = listOf(
                    CourseSuggestion(
                        name = "Machine Learning with Python",
                        platform = "edX",
                        duration_estimate = "8 weeks",
                        description = "Comprehensive ML course covering algorithms and implementation",
                        enrollment_url = "https://edx.org/learn/machine-learning"
                    )
                ),
                reasons = listOf(
                    "Your Python skills are highly relevant",
                    "Strong mathematical background matches requirements",
                    "Previous data analysis experience is a plus"
                ),
                application_deadline = "2024-02-28",
                rank = 2
            ),
            InternshipRecommendation(
                id = "intern_003",
                title = "UI/UX Design Intern",
                company = "Creative Studio",
                location = "Delhi, NCR",
                duration = "3 months",
                type = "Design",
                matchScore = 65,
                description = "Create beautiful and intuitive user experiences for our digital products.",
                requirements = listOf("Design Thinking", "Creativity", "Communication", "Problem Solving"),
                benefits = listOf("Portfolio Building", "Certificate", "Stipend", "Creative Freedom"),
                skills = listOf("Photoshop", "Illustrator", "Sketch", "InVision", "User Research"),
                salary = "20000",
                missing_skills = listOf("Figma", "Adobe XD", "Prototyping"),
                course_suggestions = listOf(
                    CourseSuggestion(
                        name = "UI/UX Design Fundamentals",
                        platform = "Skillshare",
                        duration_estimate = "5 weeks",
                        description = "Learn design principles and modern tools",
                        enrollment_url = "https://skillshare.com/ui-ux-design"
                    )
                ),
                reasons = listOf(
                    "Creative portfolio shows potential",
                    "Good understanding of user experience principles",
                    "Willingness to learn new design tools"
                ),
                application_deadline = "2024-03-10",
                rank = 3
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Internship Recommendations")
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleInternships) { internship ->
                InternshipCard(
                    internship = internship,
                    onClick = {
                        selectedInternship = internship
                    }
                )
            }
        }
    }
    
    // Show popup when internship is selected
    selectedInternship?.let { internship ->
        InternshipDetailsPopup(
            internship = internship,
            onDismiss = {
                selectedInternship = null
            },
            onApply = { internshipId ->
                // Handle apply action
                selectedInternship = null
            },
            onCompare = { internshipId ->
                comparingInternships = if (internshipId in comparingInternships) {
                    comparingInternships - internshipId
                } else {
                    comparingInternships + internshipId
                }
            },
            onShare = { internshipId ->
                // Handle share action
            },
            isComparing = internship.internship_id in comparingInternships
        )
    }
}
