package com.pmis.app.components

import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.pmis.app.ui.theme.CTAOrange

@Composable
fun ElevenLabsConvAIWidget(
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
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
                    background: transparent;
                    height: 100vh;
                    overflow: hidden;
                }
                .container {
                    height: 100vh;
                    display: flex;
                    flex-direction: column;
                }
                .loading {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100%;
                    color: #666;
                    font-size: 14px;
                    text-align: center;
                }
                .error {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100%;
                    color: #d32f2f;
                    font-size: 14px;
                    text-align: center;
                    padding: 16px;
                }
                .convai-widget {
                    width: 100%;
                    height: 100%;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="loading" id="loading">
                    Loading AI Assistant...
                </div>
                <div class="error" id="error" style="display: none;">
                    Unable to load AI Assistant. Please check your connection.
                </div>
                <div class="convai-widget" id="convai-container"></div>
            </div>
            
            <!-- Direct ElevenLabs ConvAI widget element -->
            <elevenlabs-convai agent-id="agent_9201k5sry698e14sf58cpbyfrdkx"></elevenlabs-convai>
            
            <script>
                // Load ElevenLabs ConvAI widget script
                (function() {
                    const script = document.createElement('script');
                    script.src = 'https://unpkg.com/@elevenlabs/convai-widget-embed';
                    script.async = true;
                    script.type = 'text/javascript';
                    
                    script.onload = function() {
                        document.getElementById('loading').style.display = 'none';
                        console.log('ConvAI Widget script loaded successfully');
                    };
                    
                    script.onerror = function() {
                        document.getElementById('loading').style.display = 'none';
                        document.getElementById('error').style.display = 'flex';
                        console.error('Failed to load ConvAI script');
                    };
                    
                    document.head.appendChild(script);
                })();
            </script>
        </body>
        </html>
        """.trimIndent()
    }
    
    Box(
        modifier = modifier
            .size(
                width = if (isExpanded) 400.dp else 64.dp,
                height = if (isExpanded) 600.dp else 64.dp
            )
            .zIndex(1000f), // Ensure it appears above other content
        contentAlignment = Alignment.BottomEnd
    ) {
        if (isExpanded) {
            // Full WebView when expanded
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
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
                                cacheMode = WebSettings.LOAD_DEFAULT
                                databaseEnabled = true
                                setGeolocationEnabled(false)
                                userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
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
                                    // Handle errors gracefully - let the JavaScript handle retries
                                }
                                
                                override fun onReceivedHttpError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    errorResponse: WebResourceResponse?
                                ) {
                                    super.onReceivedHttpError(view, request, errorResponse)
                                    // Handle HTTP errors gracefully
                                }
                                
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    // Allow all URLs to load within the WebView
                                    return false
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
                                
                                override fun onPermissionRequest(request: PermissionRequest?) {
                                    // Grant permissions for microphone access
                                    request?.grant(request.resources)
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Floating button when collapsed
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
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Open AI Chatbot",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun ElevenLabsWidgetOverlay(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        ElevenLabsConvAIWidget(
            isExpanded = isExpanded,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isExpanded = !isExpanded
            }
        )
    }
}
