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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.PurpleEnd
import com.pmis.app.ui.theme.CTAOrange
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Date,
    val type: NotificationType,
    val isRead: Boolean = false,
    val action: String? = null
)

enum class NotificationType {
    RECOMMENDATION, APPLICATION, REMINDER, UPDATE, SUCCESS
}

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {}
) {
    var notifications by remember { mutableStateOf(generateMockNotifications()) }
    var selectedFilter by remember { mutableStateOf("All") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        NotificationHeader(
            onBackClick = onBackClick,
            totalNotifications = notifications.size,
            unreadCount = notifications.count { !it.isRead }
        )
        
        // Filter Section
        FilterSection(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )
        
        // Notifications List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredNotifications = when (selectedFilter) {
                "Unread" -> notifications.filter { !it.isRead }
                "Recommendations" -> notifications.filter { it.type == NotificationType.RECOMMENDATION }
                "Applications" -> notifications.filter { it.type == NotificationType.APPLICATION }
                "Updates" -> notifications.filter { it.type == NotificationType.UPDATE }
                else -> notifications
            }
            
            items(filteredNotifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        // Mark as read
                        notifications = notifications.map { 
                            if (it.id == notification.id) it.copy(isRead = true) else it 
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationHeader(
    onBackClick: () -> Unit,
    totalNotifications: Int,
    unreadCount: Int
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
                    androidx.compose.ui.graphics.Brush.verticalGradient(
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
                    
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(CTAOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unreadCount.toString(),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "$totalNotifications total notifications",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All", "Unread", "Recommendations", "Applications", "Updates")
    
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
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 1.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getNotificationTypeColor(notification.type).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationTypeIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationTypeColor(notification.type),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Notification content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PurpleStart)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTimestamp(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    
                    if (notification.action != null) {
                        TextButton(
                            onClick = { /* Handle action */ },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = PurpleStart
                            )
                        ) {
                            Text(
                                text = notification.action,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getNotificationTypeIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.RECOMMENDATION -> Icons.Default.Star
        NotificationType.APPLICATION -> Icons.AutoMirrored.Filled.List
        NotificationType.REMINDER -> Icons.Default.Info
        NotificationType.UPDATE -> Icons.Default.Refresh
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
    }
}

private fun getNotificationTypeColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.RECOMMENDATION -> CTAOrange
        NotificationType.APPLICATION -> PurpleStart
        NotificationType.REMINDER -> Color(0xFFFF9800)
        NotificationType.UPDATE -> PurpleEnd
        NotificationType.SUCCESS -> Color(0xFF4CAF50)
    }
}

private fun formatTimestamp(timestamp: Date): String {
    val now = Date()
    val diff = now.time - timestamp.time
    
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(timestamp)
    }
}

private fun generateMockNotifications(): List<Notification> {
    val now = Date()
    return listOf(
        Notification(
            id = "1",
            title = "New Recommendation",
            message = "We found 3 new internship opportunities that match your profile",
            timestamp = Date(now.time - 30 * 60 * 1000), // 30 minutes ago
            type = NotificationType.RECOMMENDATION,
            isRead = false,
            action = "View"
        ),
        Notification(
            id = "2",
            title = "Application Status Update",
            message = "Your application for Software Developer at TechCorp has been reviewed",
            timestamp = Date(now.time - 2 * 60 * 60 * 1000), // 2 hours ago
            type = NotificationType.APPLICATION,
            isRead = false,
            action = "Check Status"
        ),
        Notification(
            id = "3",
            title = "Profile Reminder",
            message = "Complete your profile to get better recommendations",
            timestamp = Date(now.time - 24 * 60 * 60 * 1000), // 1 day ago
            type = NotificationType.REMINDER,
            isRead = true,
            action = "Complete"
        ),
        Notification(
            id = "4",
            title = "Application Submitted",
            message = "Your application for Data Science Intern at DataInsights has been submitted successfully",
            timestamp = Date(now.time - 2 * 24 * 60 * 60 * 1000), // 2 days ago
            type = NotificationType.SUCCESS,
            isRead = true
        ),
        Notification(
            id = "5",
            title = "System Update",
            message = "New features added: Enhanced AI recommendations and better matching algorithm",
            timestamp = Date(now.time - 3 * 24 * 60 * 60 * 1000), // 3 days ago
            type = NotificationType.UPDATE,
            isRead = true
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun NotificationsScreenPreview() {
    PMISAppTheme {
        NotificationsScreen()
    }
}

