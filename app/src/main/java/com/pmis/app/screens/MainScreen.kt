package com.pmis.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import com.pmis.app.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmis.app.ui.theme.PMISAppTheme
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.WhiteColor
import com.pmis.app.ui.components.HeroSection
import com.pmis.app.data.AppState
import kotlinx.coroutines.launch

// Navigation menu items
data class DrawerMenuItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToScreen: (String) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("home") }
    
    val menuItems = listOf(
        DrawerMenuItem("Home", "home", Icons.Default.Home),
        DrawerMenuItem("Dashboard", "dashboard", Icons.Default.Home),
        DrawerMenuItem("Intern", "intern", Icons.Default.Person),
        DrawerMenuItem("Recommendation", "recommendation", Icons.Default.Settings),
        DrawerMenuItem("Projects", "project_management", Icons.Default.Star),
        DrawerMenuItem("Notifications", "notifications", Icons.Default.Notifications)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "PM Internship Scheme",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Navigate through the app",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Navigation Items
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        selected = selectedItem == item.route,
                        onClick = {
                            selectedItem = item.route
                            scope.launch {
                                drawerState.close()
                            }
                            onNavigateToScreen(item.route)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        // Empty title - logos will be in navigationIcon and actions
                    },
                    navigationIcon = {
                        Row(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Menu button
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // PMIS Logo
                            Image(
                                painter = painterResource(id = R.drawable.ic_pmis_logo),
                                contentDescription = "PMIS logo",
                                modifier = Modifier.size(width = 80.dp, height = 24.dp),
                                contentScale = ContentScale.Fit
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // MCA Logo
                            Image(
                                painter = painterResource(id = R.drawable.ic_mca_logo),
                                contentDescription = "MCA logo",
                                modifier = Modifier.size(width = 60.dp, height = 20.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    actions = {
                        // Dashboard Logo
                        IconButton(
                            onClick = {
                                selectedItem = "dashboard"
                                onNavigateToScreen("dashboard")
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_dashboard_logo),
                                contentDescription = "Dashboard logo",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PurpleStart.copy(alpha = 0.9f),
                        titleContentColor = WhiteColor,
                        navigationIconContentColor = WhiteColor,
                        actionIconContentColor = WhiteColor
                    )
                )
            }
        ) { paddingValues ->
            // Main Content - show different content based on selected item
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedItem) {
                    "home" -> HomeScreen(
                        onNavigateToScreen = { route ->
                            onNavigateToScreen(route)
                        }
                    )
                    "dashboard" -> DashboardScreen(
                        onNavigateToScreen = { route ->
                            onNavigateToScreen(route)
                        },
                        onBackClick = {
                            selectedItem = "home"
                        }
                    )
                    "intern" -> InternRegistrationScreen(
                        onNavigateToScreen = { route ->
                            onNavigateToScreen(route)
                        }
                    )
                    "recommendation" -> RecommendationsScreen(
                        internFormState = AppState.internFormData,
                        onBackClick = { /* Handle back */ }
                    )
                    "project_management" -> ProjectManagementScreen()
                    "notifications" -> NotificationsScreen()
                    else -> HeroSection()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PMISAppTheme {
        MainScreen()
    }
}