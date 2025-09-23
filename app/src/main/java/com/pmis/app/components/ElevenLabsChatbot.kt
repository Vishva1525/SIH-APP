package com.pmis.app.components

import android.webkit.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import androidx.compose.ui.viewinterop.AndroidView
import com.pmis.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ElevenLabsChatbotButton(
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
                imageVector = Icons.AutoMirrored.Filled.Chat,
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
fun ElevenLabsChatbotWidget(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // HTML content for ElevenLabs ConvAI widget
    val htmlContent = remember {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>PMIS AI Assistant</title>
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    background: linear-gradient(135deg, #FF6F00 0%, #FFA000 100%);
                    height: 100vh;
                    overflow: hidden;
                }
                .container {
                    height: 100vh;
                    display: flex;
                    flex-direction: column;
                }
                .header {
                    background: rgba(255, 255, 255, 0.1);
                    padding: 16px;
                    text-align: center;
                    color: white;
                    font-weight: bold;
                    font-size: 18px;
                }
                .chat-container {
                    flex: 1;
                    background: white;
                    margin: 8px;
                    border-radius: 12px;
                    overflow: hidden;
                }
                .loading {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100%;
                    color: #666;
                    font-size: 16px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    PMIS AI Assistant
                </div>
                <div class="chat-container">
                    <div class="loading" id="loading">
                        Loading AI Assistant...
                    </div>
                </div>
            </div>
            
            <script>
                // Load ElevenLabs ConvAI widget
                (function() {
                    const script = document.createElement('script');
                    script.src = 'https://unpkg.com/@elevenlabs/convai-widget-embed';
                    script.async = true;
                    script.type = 'text/javascript';
                    
                    script.onload = function() {
                        // Initialize the ConvAI widget
                        if (window.ElevenLabsConvAI) {
                            const widget = new window.ElevenLabsConvAI({
                                agentId: 'agent_9201k5sry698e14sf58cpbyfrdkx',
                                container: document.querySelector('.chat-container'),
                                theme: {
                                    primaryColor: '#FF6F00',
                                    backgroundColor: '#ffffff',
                                    textColor: '#333333'
                                },
                                onReady: function() {
                                    document.getElementById('loading').style.display = 'none';
                                },
                                onError: function(error) {
                                    document.getElementById('loading').innerHTML = 
                                        'Unable to load AI Assistant. Please try again.';
                                    console.error('ConvAI Widget Error:', error);
                                }
                            });
                        } else {
                            document.getElementById('loading').innerHTML = 
                                'Failed to load AI Assistant. Please check your connection.';
                        }
                    };
                    
                    script.onerror = function() {
                        document.getElementById('loading').innerHTML = 
                            'Failed to load AI Assistant. Please check your connection.';
                    };
                    
                    document.head.appendChild(script);
                })();
            </script>
        </body>
        </html>
        """.trimIndent()
    }
    
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(CTAOrange, CTAOrange.copy(alpha = 0.8f))
                        )
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PMIS AI Assistant",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close chatbot",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // WebView for ElevenLabs widget
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = false
                            displayZoomControls = false
                            setSupportZoom(false)
                            allowFileAccess = true
                            allowContentAccess = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Page loaded successfully
                            }
                            
                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                // Handle errors gracefully
                            }
                        }
                        
                        webChromeClient = object : WebChromeClient() {
                            override fun onJsAlert(
                                view: WebView?,
                                url: String?,
                                message: String?,
                                result: JsResult?
                            ): Boolean {
                                // Handle JavaScript alerts
                                result?.confirm()
                                return true
                            }
                        }
                        
                        loadDataWithBaseURL(
                            "https://elevenlabs.io",
                            htmlContent,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun ElevenLabsChatbotWithState(
    modifier: Modifier = Modifier
) {
    var showChatbot by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Show chatbot widget when activated
        if (showChatbot) {
            ElevenLabsChatbotWidget(
                onClose = { showChatbot = false },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Show floating button
            ElevenLabsChatbotButton(
                onChatbotClick = { showChatbot = true },
                modifier = Modifier
            )
        }
    }
}
