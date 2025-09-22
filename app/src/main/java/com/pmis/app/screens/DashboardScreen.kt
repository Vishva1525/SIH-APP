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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.PurpleEnd
import com.pmis.app.ui.theme.CTAOrange
import com.pmis.app.viewmodel.DashboardViewModel
import com.pmis.app.viewmodel.DashboardSummary
import com.pmis.app.viewmodel.ActivityItem
import com.pmis.app.viewmodel.ActivityType
import kotlinx.coroutines.delay

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


// Safe navigation helper to prevent crashes from rapid clicks
@Composable
private fun rememberSafeNavigation(
    onNavigateToScreen: (String) -> Unit
): (String) -> Unit {
    var lastClickTime by remember { mutableStateOf(0L) }
    val debounceTime = 500L // 500ms debounce
    
    return remember(onNavigateToScreen) {
        { route: String ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > debounceTime) {
                lastClickTime = currentTime
                try {
                    onNavigateToScreen(route)
                } catch (e: Exception) {
                    // Log error but don't crash
                    android.util.Log.e("DashboardScreen", "Navigation error: ${e.message}", e)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToScreen: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val viewModel = remember { DashboardViewModel() }
    val summary by viewModel.summary.collectAsState()
    val activities by viewModel.activities.collectAsState()
    
    // Use safe navigation to prevent crashes
    val safeNavigate = rememberSafeNavigation(onNavigateToScreen)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.semantics { contentDescription = "Back" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { safeNavigate("notifications") },
                        modifier = Modifier.semantics { contentDescription = "Notifications" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                DashboardHeader()
            }
            
            // Stats Cards
            item {
                StatsSection(
                    summary = summary,
                    onActiveApplicationsClick = { safeNavigate("applications") },
                    onRecommendationsClick = { safeNavigate("ml_recommendations") }
                )
            }
            
            // Quick Actions
            item {
                QuickActionsSection(
                    onNavigateToScreen = safeNavigate,
                    onGetRecommendations = { viewModel.getRecommendations("quick_action") }
                )
            }
            
            // Recent Activity
            item {
                RecentActivitySection(
                    activities = activities,
                    onActivityClick = { activity ->
                        handleActivityClick(activity, safeNavigate)
                    },
                    onViewAllClick = { safeNavigate("activity_feed") }
                )
            }
            
            // Progress Overview
            item {
                ProgressOverviewSection(
                    profileCompletion = summary.profileCompletion,
                    onProfileClick = { safeNavigate("profile_edit") }
                )
            }
        }
    }
}

private fun handleActivityClick(activity: ActivityItem, onNavigateToScreen: (String) -> Unit) {
    when (activity.type) {
        ActivityType.RECOMMENDATION -> {
            val jobId = activity.payload["jobId"] as? String
            onNavigateToScreen("recommendation_details?jobId=$jobId")
        }
        ActivityType.APPLICATION -> {
            val applicationId = activity.payload["applicationId"] as? String
            onNavigateToScreen("application_details?applicationId=$applicationId")
        }
        ActivityType.UPDATE -> {
            onNavigateToScreen("profile_history")
        }
        ActivityType.INTERNSHIP -> {
            onNavigateToScreen("internship_details")
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
private fun StatsSection(
    summary: DashboardSummary,
    onActiveApplicationsClick: () -> Unit = {},
    onRecommendationsClick: () -> Unit = {}
) {
    val stats = listOf(
        DashboardStat(
            title = "Active Applications",
            value = summary.activeApplicationsCount.toString(),
            icon = Icons.AutoMirrored.Filled.List,
            color = PurpleStart,
            trend = "+2 this week"
        ),
        DashboardStat(
            title = "Recommendations",
            value = summary.recommendationsCount.toString(),
            icon = Icons.Default.Star,
            color = CTAOrange,
            trend = "New matches"
        ),
        DashboardStat(
            title = "Profile Views",
            value = summary.profileViews.toString(),
            icon = Icons.Default.Info,
            color = PurpleEnd,
            trend = "+15%"
        ),
        DashboardStat(
            title = "Completion",
            value = "${(summary.profileCompletion * 100).toInt()}%",
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
            StatCard(
                stat = stat,
                onClick = when (stat.title) {
                    "Active Applications" -> onActiveApplicationsClick
                    "Recommendations" -> onRecommendationsClick
                    else -> { -> }
                }
            )
        }
    }
}

@Composable
private fun StatCard(
    stat: DashboardStat,
    onClick: () -> Unit = {}
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(stat) {
        animatedValue = 1f
    }
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clickable { onClick() }
            .semantics { 
                contentDescription = "${stat.title}: ${stat.value}"
            },
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
    onNavigateToScreen: (String) -> Unit,
    onGetRecommendations: () -> Unit = {}
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
                route = "internships_search"
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
                route = "profile_edit"
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
                    onClick = { 
                        if (action.title == "Get Recommendations") {
                            onGetRecommendations()
                        }
                        onNavigateToScreen(action.route) 
                    }
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
    val actionId = when (action.title) {
        "Find Internships" -> "quickFind"
        "Get Recommendations" -> "quickRecommend"
        "Update Profile" -> "quickProfile"
        "View Applications" -> "quickApplications"
        else -> "quick${action.title.replace(" ", "")}"
    }
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable { onClick() }
            .semantics { 
                contentDescription = "${action.title}: ${action.description}"
            },
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
private fun RecentActivitySection(
    activities: List<ActivityItem>,
    onActivityClick: (ActivityItem) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
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
            TextButton(
                onClick = onViewAllClick,
                modifier = Modifier.semantics { contentDescription = "View All Activity" }
            ) {
                Text("View All")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        activities.forEach { activity ->
            ActivityItem(
                activity = activity,
                onClick = { onActivityClick(activity) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActivityItem(
    activity: ActivityItem,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .semantics { 
                contentDescription = "${activity.title}: ${activity.description}"
            },
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
private fun ProgressOverviewSection(
    profileCompletion: Float = 0.85f,
    onProfileClick: () -> Unit = {}
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onProfileClick() }
                .semantics { 
                    contentDescription = "Profile Completion: ${(profileCompletion * 100).toInt()}%"
                },
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
                        text = "${(profileCompletion * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = PurpleStart
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { profileCompletion },
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

