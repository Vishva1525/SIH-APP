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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.PurpleEnd
import com.pmis.app.ui.theme.CTAOrange
import java.text.SimpleDateFormat
import java.util.*

data class Project(
    val id: String,
    val title: String,
    val description: String,
    val company: String,
    val status: ProjectStatus,
    val progress: Int, // 0-100
    val startDate: Date,
    val endDate: Date?,
    val tasks: List<Task>,
    val teamMembers: List<String>,
    val priority: Priority,
    val tags: List<String>
)

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: Priority,
    val assignedTo: String,
    val dueDate: Date?,
    val completedDate: Date?,
    val estimatedHours: Int?,
    val actualHours: Int?
)

enum class ProjectStatus {
    PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED
}

enum class TaskStatus {
    TODO, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED
}

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

@Composable
fun ProjectManagementScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Overview") }
    var projects by remember { mutableStateOf(generateMockProjects()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        ProjectManagementHeader(
            onBackClick = onBackClick,
            totalProjects = projects.size
        )
        
        // Tab Navigation
        TabNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // Content based on selected tab
        when (selectedTab) {
            "Overview" -> OverviewTab(projects = projects)
            "Projects" -> ProjectsTab(projects = projects)
            "Tasks" -> TasksTab(projects = projects)
            "Timeline" -> TimelineTab(projects = projects)
        }
    }
}

@Composable
private fun ProjectManagementHeader(
    onBackClick: () -> Unit,
    @Suppress("UNUSED_PARAMETER") totalProjects: Int
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
                    
                    Text(
                        text = "Project Management",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(40.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Manage your internship projects and track progress",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun TabNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("Overview", "Projects", "Tasks", "Timeline")
    
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs) { tab ->
            FilterChip(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                label = { Text(tab) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PurpleStart,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun OverviewTab(projects: List<Project>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Cards
        item {
            StatsOverview(projects = projects)
        }
        
        // Recent Projects
        item {
            RecentProjectsSection(projects = projects)
        }
        
        // Progress Overview
        item {
            ProgressOverviewSection(projects = projects)
        }
    }
}

@Composable
private fun StatsOverview(projects: List<Project>) {
    val totalProjects = projects.size
    val activeProjects = projects.count { it.status == ProjectStatus.IN_PROGRESS }
    val completedProjects = projects.count { it.status == ProjectStatus.COMPLETED }
    val totalTasks = projects.sumOf { it.tasks.size }
    val completedTasks = projects.sumOf { it.tasks.count { task -> task.status == TaskStatus.COMPLETED } }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            StatCard(
                title = "Total Projects",
                value = totalProjects.toString(),
                icon = Icons.Default.Star,
                color = PurpleStart
            )
        }
        item {
            StatCard(
                title = "Active",
                value = activeProjects.toString(),
                icon = Icons.Default.PlayArrow,
                color = CTAOrange
            )
        }
        item {
            StatCard(
                title = "Completed",
                value = completedProjects.toString(),
                icon = Icons.Default.CheckCircle,
                color = Color(0xFF4CAF50)
            )
        }
        item {
            StatCard(
                title = "Tasks Done",
                value = "$completedTasks/$totalTasks",
                icon = Icons.AutoMirrored.Filled.List,
                color = PurpleEnd
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RecentProjectsSection(projects: List<Project>) {
    Column {
        Text(
            text = "Recent Projects",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        val recentProjects = projects.take(3)
        recentProjects.forEach { project ->
            ProjectCard(project = project)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProjectCard(project: Project) {
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = project.company,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PurpleStart
                    )
                }
                
                StatusChip(status = project.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${project.progress}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { project.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PurpleStart,
                    trackColor = PurpleStart.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: ProjectStatus) {
    val (color, text) = when (status) {
        ProjectStatus.PLANNING -> Color(0xFFFF9800) to "Planning"
        ProjectStatus.IN_PROGRESS -> CTAOrange to "In Progress"
        ProjectStatus.ON_HOLD -> Color(0xFF9E9E9E) to "On Hold"
        ProjectStatus.COMPLETED -> Color(0xFF4CAF50) to "Completed"
        ProjectStatus.CANCELLED -> Color(0xFFF44336) to "Cancelled"
    }
    
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = color
        )
    }
}

@Composable
private fun ProgressOverviewSection(projects: List<Project>) {
    Column {
        Text(
            text = "Overall Progress",
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
                val overallProgress = if (projects.isNotEmpty()) {
                    projects.map { it.progress }.average().toInt()
                } else 0
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completion Rate",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "$overallProgress%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = PurpleStart
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { overallProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PurpleStart,
                    trackColor = PurpleStart.copy(alpha = 0.2f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Keep up the great work! You're making excellent progress on your internship projects.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProjectsTab(projects: List<Project>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects) { project ->
            ProjectCard(project = project)
        }
    }
}

@Composable
private fun TasksTab(projects: List<Project>) {
    val allTasks = projects.flatMap { project ->
        project.tasks.map { task -> task to project }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(allTasks) { (task, project) ->
            TaskCard(task = task, project = project)
        }
    }
}

@Composable
private fun TaskCard(task: Task, project: Project) {
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
            Checkbox(
                checked = task.status == TaskStatus.COMPLETED,
                onCheckedChange = { /* Handle completion */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = PurpleStart
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            
            PriorityChip(priority = task.priority)
        }
    }
}

@Composable
private fun PriorityChip(priority: Priority) {
    val (color, text) = when (priority) {
        Priority.LOW -> Color(0xFF4CAF50) to "Low"
        Priority.MEDIUM -> Color(0xFFFF9800) to "Med"
        Priority.HIGH -> CTAOrange to "High"
        Priority.URGENT -> Color(0xFFF44336) to "Urgent"
    }
    
    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = color
        )
    }
}

@Composable
private fun TimelineTab(@Suppress("UNUSED_PARAMETER") projects: List<Project>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Project Timeline",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        // Timeline items would go here
        item {
            Text(
                text = "Timeline view coming soon...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

private fun generateMockProjects(): List<Project> {
    val now = Date()
    return listOf(
        Project(
            id = "1",
            title = "E-commerce Website",
            description = "Develop a full-stack e-commerce platform with React and Node.js",
            company = "TechCorp Solutions",
            status = ProjectStatus.IN_PROGRESS,
            progress = 65,
            startDate = Date(now.time - 30L * 24 * 60 * 60 * 1000), // 30 days ago
            endDate = Date(now.time + 30L * 24 * 60 * 60 * 1000), // 30 days from now
            tasks = listOf(
                Task("1", "Setup project structure", "Initialize React app", TaskStatus.COMPLETED, Priority.HIGH, "You", Date(now.time - 25L * 24 * 60 * 60 * 1000), Date(now.time - 20L * 24 * 60 * 60 * 1000), 8, 6),
                Task("2", "Design UI components", "Create reusable components", TaskStatus.IN_PROGRESS, Priority.MEDIUM, "You", Date(now.time + 5L * 24 * 60 * 60 * 1000), null, 16, 10),
                Task("3", "Implement backend API", "Create REST API endpoints", TaskStatus.TODO, Priority.HIGH, "You", Date(now.time + 15L * 24 * 60 * 60 * 1000), null, 24, null)
            ),
            teamMembers = listOf("You", "John Doe", "Jane Smith"),
            priority = Priority.HIGH,
            tags = listOf("React", "Node.js", "E-commerce")
        ),
        Project(
            id = "2",
            title = "Data Analysis Dashboard",
            description = "Create interactive dashboards for business analytics",
            company = "DataInsights Pvt Ltd",
            status = ProjectStatus.PLANNING,
            progress = 20,
            startDate = Date(now.time - 7L * 24 * 60 * 60 * 1000), // 7 days ago
            endDate = Date(now.time + 45L * 24 * 60 * 60 * 1000), // 45 days from now
            tasks = listOf(
                Task("4", "Requirements gathering", "Meet with stakeholders", TaskStatus.COMPLETED, Priority.HIGH, "You", Date(now.time - 5L * 24 * 60 * 60 * 1000), Date(now.time - 3L * 24 * 60 * 60 * 1000), 4, 3),
                Task("5", "Database design", "Design data schema", TaskStatus.IN_PROGRESS, Priority.MEDIUM, "You", Date(now.time + 7L * 24 * 60 * 60 * 1000), null, 12, 4),
                Task("6", "Frontend development", "Build dashboard UI", TaskStatus.TODO, Priority.MEDIUM, "You", Date(now.time + 20L * 24 * 60 * 60 * 1000), null, 20, null)
            ),
            teamMembers = listOf("You", "Mike Johnson"),
            priority = Priority.MEDIUM,
            tags = listOf("Python", "Django", "Data Visualization")
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ProjectManagementScreenPreview() {
    PMISAppTheme {
        ProjectManagementScreen()
    }
}

