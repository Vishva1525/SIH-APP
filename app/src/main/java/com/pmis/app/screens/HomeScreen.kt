package com.pmis.app.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.animation.Crossfade
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.pmis.app.R

data class FeatureCard(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

data class Testimonial(
    val name: String,
    val role: String,
    val company: String,
    val content: String,
    val rating: Int
)

@Composable
fun HomeScreen(
    onNavigateToScreen: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        item {
            HeroSection(onNavigateToScreen = onNavigateToScreen)
        }
        
        // Features Section
        item {
            FeaturesSection(onNavigateToScreen = onNavigateToScreen)
        }
        
        // Statistics Section
        item {
            StatisticsSection()
        }
        
        // Testimonials Section
        item {
            TestimonialsSection()
        }
        
        // CTA Section
        item {
            CTASection(onNavigateToScreen = onNavigateToScreen)
        }
    }
}

@Composable
private fun HeroSection(
    onNavigateToScreen: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PurpleStart,
                            PurpleEnd
                        )
                    )
                )
                .padding(24.dp)
        ) {
            val images = remember { listOf(R.drawable.hero, R.drawable.hero2, R.drawable.hero3) }
            var currentIndex by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(3000)
                    currentIndex = (currentIndex + 1) % images.size
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "PM Internship Scheme",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Empowering students with real-world opportunities and AI-powered recommendations",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToScreen("intern") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PurpleStart
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Get Started",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        OutlinedButton(
                            onClick = { onNavigateToScreen("ml_recommendations") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Text(
                                text = "Learn More",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CTAOrange)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = currentIndex, animationSpec = tween(700)) { idx ->
                        Image(
                            painter = painterResource(id = images[idx]),
                            contentDescription = "Hero visual",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturesSection(
    onNavigateToScreen: (String) -> Unit
) {
    Column {
        Text(
            text = "Why Choose PMIS?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val features = listOf(
            FeatureCard(
                title = "AI-Powered Matching",
                description = "Get personalized internship recommendations based on your skills and preferences",
                icon = Icons.Default.Star,
                color = CTAOrange,
                route = "ml_recommendations"
            ),
            FeatureCard(
                title = "Easy Registration",
                description = "Simple step-by-step process to create your profile and start applying",
                icon = Icons.Default.Person,
                color = PurpleStart,
                route = "intern"
            ),
            FeatureCard(
                title = "Real-time Updates",
                description = "Stay informed about new opportunities and application status",
                icon = Icons.Default.Notifications,
                color = PurpleEnd,
                route = "notifications"
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(features) { feature ->
                FeatureCard(feature = feature, onClick = { onNavigateToScreen(feature.route) })
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: FeatureCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(feature.color.copy(alpha = 0.05f))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = feature.color,
                    modifier = Modifier.size(32.dp)
                )
                
                Column {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsSection() {
    Column {
        Text(
            text = "Our Impact",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val stats = listOf(
            Triple("10,000+", "Students Registered", PurpleStart),
            Triple("500+", "Partner Companies", CTAOrange),
            Triple("85%", "Success Rate", Color(0xFF4CAF50)),
            Triple("50+", "Cities Covered", PurpleEnd)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(stats) { (value, label, color) ->
                StatCard(value = value, label = label, color = color)
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color.copy(alpha = 0.1f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TestimonialsSection() {
    Column {
        Text(
            text = "What Students Say",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val testimonials = listOf(
            Testimonial(
                name = "Priya Sharma",
                role = "Computer Science Student",
                company = "IIT Delhi",
                content = "PMIS helped me find the perfect internship that aligned with my career goals. The AI recommendations were spot on!",
                rating = 5
            ),
            Testimonial(
                name = "Arjun Patel",
                role = "Engineering Student",
                company = "NIT Surat",
                content = "The platform made it so easy to apply to multiple companies. I got 3 offers within a week!",
                rating = 5
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(testimonials) { testimonial ->
                TestimonialCard(testimonial = testimonial)
            }
        }
    }
}

@Composable
private fun TestimonialCard(testimonial: Testimonial) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = testimonial.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${testimonial.role}, ${testimonial.company}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Row {
                            repeat(testimonial.rating) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CTAOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "\"${testimonial.content}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CTASection(
    onNavigateToScreen: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CTAOrange.copy(alpha = 0.1f),
                            CTAOrange.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ready to Start Your Journey?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Join thousands of students who have found their dream internships",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { onNavigateToScreen("intern") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CTAOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Get Started Now",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    PMISAppTheme {
        HomeScreen()
    }
}

