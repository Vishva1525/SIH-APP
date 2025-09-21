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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.PurpleEnd
import com.pmis.app.ui.theme.CTAOrange

data class DashboardStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val trend: String? = null
)

data class QuickAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

data class RecentActivity(
    val title: String,
    val description: String,
    val time: String,
    val type: ActivityType
)

enum class ActivityType {
    INTERNSHIP, RECOMMENDATION, APPLICATION, UPDATE
}

@Composable
fun DashboardScreen(
    onNavigateToScreen: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            DashboardHeader()
        }
        
        // Stats Cards
        item {
            StatsSection()
        }
        
        // Quick Actions
        item {
            QuickActionsSection(onNavigateToScreen = onNavigateToScreen)
        }
        
        // Recent Activity
        item {
            RecentActivitySection()
        }
        
        // Progress Overview
        item {
            ProgressOverviewSection()
        }
    }
}

@Composable
private fun DashboardHeader() {
    Column {
        Text(
            text = "Welcome back! 👋",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Here's what's happening with your internship journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StatsSection() {
    val stats = listOf(
        DashboardStat(
            title = "Active Applications",
            value = "3",
            icon = Icons.AutoMirrored.Filled.List,
            color = PurpleStart,
            trend = "+2 this week"
        ),
        DashboardStat(
            title = "Recommendations",
            value = "12",
            icon = Icons.Default.Star,
            color = CTAOrange,
            trend = "New matches"
        ),
        DashboardStat(
            title = "Profile Views",
            value = "47",
            icon = Icons.Default.Info,
            color = PurpleEnd,
            trend = "+15%"
        ),
        DashboardStat(
            title = "Completion",
            value = "85%",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF4CAF50),
            trend = "Profile complete"
        )
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(stats) { stat ->
            StatCard(stat = stat)
        }
    }
}

@Composable
private fun StatCard(stat: DashboardStat) {
    var animatedValue by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(stat) {
        animatedValue = 1f
    }
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            stat.color.copy(alpha = 0.1f),
                            stat.color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = null,
                        tint = stat.color,
                        modifier = Modifier.size(24.dp)
                    )
                    if (stat.trend != null) {
                        Text(
                            text = stat.trend,
                            style = MaterialTheme.typography.bodySmall,
                            color = stat.color,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Column {
                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stat.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToScreen: (String) -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        val quickActions = listOf(
            QuickAction(
                title = "Find Internships",
                description = "Browse available opportunities",
                icon = Icons.Default.Search,
                color = PurpleStart,
                route = "intern"
            ),
            QuickAction(
                title = "Get Recommendations",
                description = "AI-powered suggestions",
                icon = Icons.Default.Star,
                color = CTAOrange,
                route = "ml_recommendations"
            ),
            QuickAction(
                title = "Update Profile",
                description = "Keep your info current",
                icon = Icons.Default.Edit,
                color = PurpleEnd,
                route = "intern"
            ),
            QuickAction(
                title = "View Applications",
                description = "Track your progress",
                icon = Icons.AutoMirrored.Filled.List,
                color = Color(0xFF4CAF50),
                route = "applications"
            )
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(quickActions) { action ->
                QuickActionCard(
                    action = action,
                    onClick = { onNavigateToScreen(action.route) }
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(action.color.copy(alpha = 0.1f))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = action.color,
                    modifier = Modifier.size(20.dp)
                )
                
                Column {
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = action.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { /* View all */ }) {
                Text("View All")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        val activities = listOf(
            RecentActivity(
                title = "New Recommendation",
                description = "Data Science internship at TechCorp",
                time = "2 hours ago",
                type = ActivityType.RECOMMENDATION
            ),
            RecentActivity(
                title = "Application Submitted",
                description = "Software Developer role at StartupXYZ",
                time = "1 day ago",
                type = ActivityType.APPLICATION
            ),
            RecentActivity(
                title = "Profile Updated",
                description = "Added new skills and experience",
                time = "3 days ago",
                type = ActivityType.UPDATE
            )
        )
        
        activities.forEach { activity ->
            ActivityItem(activity = activity)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActivityItem(activity: RecentActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getActivityTypeColor(activity.type).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getActivityTypeIcon(activity.type),
                    contentDescription = null,
                    tint = getActivityTypeColor(activity.type),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = activity.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ProgressOverviewSection() {
    Column {
        Text(
            text = "Progress Overview",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile Completion",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "85%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = PurpleStart
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { 0.85f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PurpleStart,
                    trackColor = PurpleStart.copy(alpha = 0.2f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Complete your profile to get better recommendations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun getActivityTypeIcon(type: ActivityType): ImageVector {
    return when (type) {
        ActivityType.INTERNSHIP -> Icons.Default.Star
        ActivityType.RECOMMENDATION -> Icons.Default.Star
        ActivityType.APPLICATION -> Icons.AutoMirrored.Filled.List
        ActivityType.UPDATE -> Icons.Default.Refresh
    }
}

private fun getActivityTypeColor(type: ActivityType): Color {
    return when (type) {
        ActivityType.INTERNSHIP -> PurpleStart
        ActivityType.RECOMMENDATION -> CTAOrange
        ActivityType.APPLICATION -> Color(0xFF4CAF50)
        ActivityType.UPDATE -> PurpleEnd
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    PMISAppTheme {
        DashboardScreen()
    }
}

