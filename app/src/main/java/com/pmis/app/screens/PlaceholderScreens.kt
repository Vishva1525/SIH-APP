package com.pmis.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.ui.theme.PMISAppTheme

@Composable
fun PlaceholderScreen(
    title: String,
    description: String = "This is a placeholder screen. Content will be added here in future updates."
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun AboutScreen() {
    PlaceholderScreen(
        title = "About the Scheme",
        description = "Learn about the PM Internship Scheme, its objectives, benefits, and how it empowers students with real-world experience in various industries."
    )
}

@Composable
fun RegisterScreen() {
    PlaceholderScreen(
        title = "Apply/Register",
        description = "Register for the PM Internship Scheme. Fill out your application, upload documents, and track your application status."
    )
}

@Composable
fun StudentsScreen() {
    PlaceholderScreen(
        title = "For Students",
        description = "Resources, guidelines, and opportunities specifically designed for students participating in the PM Internship Scheme."
    )
}

@Composable
fun EmployersScreen() {
    PlaceholderScreen(
        title = "For Employers",
        description = "Information for employers looking to participate in the PM Internship Scheme and provide internship opportunities."
    )
}

@Composable
fun GuidelinesScreen() {
    PlaceholderScreen(
        title = "Guidelines/FAQs",
        description = "Frequently asked questions, guidelines, and helpful information about the PM Internship Scheme."
    )
}

@Composable
fun ContactScreen() {
    PlaceholderScreen(
        title = "Contact Us",
        description = "Get in touch with us for any queries, support, or assistance related to the PM Internship Scheme."
    )
}

@Composable
fun HomeScreen() {
    PlaceholderScreen(
        title = "Home",
        description = "Welcome to the PM Internship Scheme portal. Your gateway to meaningful internship opportunities and career development."
    )
}

@Composable
fun InternScreen() {
    InternRegistrationScreen()
}

@Composable
fun RecommendationScreen() {
    PlaceholderScreen(
        title = "Recommendations",
        description = "Discover personalized internship recommendations based on your skills, interests, and career goals. Find the perfect match for your professional journey."
    )
}

@Preview(showBackground = true)
@Composable
fun PlaceholderScreenPreview() {
    PMISAppTheme {
        AboutScreen()
    }
}
