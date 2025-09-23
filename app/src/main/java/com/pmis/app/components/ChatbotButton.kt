package com.pmis.app.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.ui.theme.*
import com.pmis.app.services.ChatbotService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatbotButton(
    onChatbotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showPulse by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    // Pulse animation
    val pulseScale by animateFloatAsState(
        targetValue = if (showPulse) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Expand animation
    val expandScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "expand"
    )
    
    // Auto-hide pulse after 5 seconds
    LaunchedEffect(Unit) {
        delay(5000)
        showPulse = false
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Pulse ring
        if (showPulse) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CTAOrange.copy(alpha = 0.3f),
                                CTAOrange.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
                    .scale(pulseScale)
            )
        }
        
        // Main chatbot button
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = CTAOrange.copy(alpha = 0.3f),
                    spotColor = CTAOrange.copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CTAOrange, CTAOrange.copy(alpha = 0.8f))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isExpanded = true
                    onChatbotClick()
                    // Reset expansion after animation
                    coroutineScope.launch {
                        delay(200)
                        isExpanded = false
                    }
                }
                .scale(expandScale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Open AI Chatbot",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        // Tooltip text
        AnimatedVisibility(
            visible = showPulse,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
        ) {
            Card(
                modifier = Modifier
                    .padding(end = 80.dp, bottom = 8.dp)
                    .width(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Need help? Ask our AI assistant!",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun ChatbotButtonWithState(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    fun handleChatbotClick() {
        isLoading = true
        // Call the n8n webhook
        coroutineScope.launch {
            ChatbotService.triggerChatbot(context) {
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = CTAOrange,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        ChatbotButton(
            onChatbotClick = ::handleChatbotClick,
            modifier = Modifier
        )
    }
}
