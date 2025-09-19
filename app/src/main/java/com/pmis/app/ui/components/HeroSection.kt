package com.pmis.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.R
import com.pmis.app.ui.theme.CTAOrange
import com.pmis.app.ui.theme.PurpleEnd
import com.pmis.app.ui.theme.PurpleStart
import com.pmis.app.ui.theme.WhiteColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun HeroSection(modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val showImage = screenWidth >= 420
    
    // Animation state for CTA button
    var showButton by remember { mutableStateOf(false) }
    
    // Responsive height - smaller for small screens without image
    val heroHeight = if (showImage) 400.dp else 320.dp
    
    // Trigger button animation after a delay
    LaunchedEffect(Unit) {
        delay(800) // Wait 800ms before showing button
        showButton = true
    }

    val gradient = Brush.linearGradient(
        colors = listOf(PurpleStart, PurpleEnd),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heroHeight)
            .background(brush = gradient)
            .padding(20.dp)
            .semantics {
                contentDescription = "PM Internship Scheme hero section with offer acceptance information"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(if (showImage) 0.62f else 1.0f)
                    .padding(end = if (showImage) 8.dp else 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Accepted the Offer",
                    color = WhiteColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.semantics {
                        contentDescription = "Status: Accepted the Offer"
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "But not Joined yet ?",
                    color = WhiteColor,
                    fontSize = if (screenWidth >= 600) 44.sp else 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics {
                        heading()
                        contentDescription = "Main question: But not Joined yet?"
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Orange capsule with accessibility
                Box(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(CTAOrange)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .semantics {
                            contentDescription = "Important message: Don't worry, the window is still open!"
                        }
                ) {
                    Text(
                        text = "Don't worry–the window is still open!",
                        color = WhiteColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Reach out to the company that made you the offer. Contact details are in your offer letter.",
                    color = WhiteColor,
                    fontSize = 14.sp,
                    modifier = Modifier.semantics {
                        contentDescription = "Instructions: Reach out to the company that made you the offer. Contact details are in your offer letter."
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))

                // Animated CTA Button
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn(
                        animationSpec = tween(600)
                    ) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(600)
                    )
                ) {
                    Button(
                        onClick = { /*TODO: wire action*/ },
                        colors = ButtonDefaults.buttonColors(containerColor = CTAOrange),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(20.dp))
                            .semantics {
                                contentDescription = "Reach Out Now button. Tap to contact the company about your offer."
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_contact_cta),
                            contentDescription = null, // Decorative icon, button has description
                            tint = WhiteColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reach Out Now",
                            color = WhiteColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp // Explicit sp for accessibility
                        )
                    }
                }
            }

            // Right side image container - only shown on larger screens
            if (showImage) {
                Box(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight()
                        .padding(start = 6.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pm_placeholder), // Use pm_modi when available
                        contentDescription = "Prime Minister Modi, representing the PM Internship Scheme leadership",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(280.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .shadow(8.dp, RoundedCornerShape(24.dp))
                            .semantics {
                                contentDescription = "Decorative image of Prime Minister Modi representing the PM Internship Scheme"
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeroSectionPreviewLight() {
    Surface {
        HeroSection()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HeroSectionPreviewSmall() {
    Surface {
        HeroSection()
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 480)
@Composable
fun HeroSectionPreviewLarge() {
    Surface {
        HeroSection()
    }
}

@Preview(
    showBackground = true, 
    widthDp = 360, 
    heightDp = 640,
    fontScale = 1.5f,
    name = "Large Text Scale"
)
@Composable
fun HeroSectionPreviewLargeText() {
    Surface {
        HeroSection()
    }
}

@Preview(
    showBackground = true, 
    widthDp = 360, 
    heightDp = 640,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode Small Screen"
)
@Composable
fun HeroSectionPreviewDarkSmall() {
    Surface {
        HeroSection()
    }
}
