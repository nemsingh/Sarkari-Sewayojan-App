package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryRed

data class SocialChannel(
    val name: String,
    val description: String,
    val brandColor: Color,
    val gradientColors: List<Color>? = null,
    val subscribers: String,
    val handle: String,
    val url: String,
    val logoType: LogoType
)

enum class LogoType {
    WHATSAPP, INSTAGRAM, YOUTUBE, TELEGRAM, FACEBOOK, LINKEDIN
}

@Composable
fun FollowUsScreen(
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val channels = listOf(
        SocialChannel(
            name = "WhatsApp Channel",
            description = "Get instant Govt Job notifications & daily PDF alerts directly on WhatsApp.",
            brandColor = Color(0xFF25D366),
            subscribers = "Join Channel Now",
            handle = "@SewayojanOfficial",
            url = "https://whatsapp.com/channel/0029Vb7mSRl6xCSTsZCzb60Y",
            logoType = LogoType.WHATSAPP
        ),
        SocialChannel(
            name = "Instagram",
            description = "Daily General Knowledge reels, job updates & current affairs quizzes.",
            brandColor = Color(0xFFE1306C),
            gradientColors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045)),
            subscribers = "Follow Now",
            handle = "@sewayojan_gov_jobs",
            url = "https://whatsapp.com/channel/0029Vb7mSRl6xCSTsZCzb60Y",
            logoType = LogoType.INSTAGRAM
        ),
        SocialChannel(
            name = "YouTube",
            description = "Live exam syllabus discussions, admit card release & form fill guides.",
            brandColor = Color(0xFFFF0000),
            subscribers = "Subscribe Now",
            handle = "Sewayojan Official",
            url = "https://whatsapp.com/channel/0029Vb7mSRl6xCSTsZCzb60Y",
            logoType = LogoType.YOUTUBE
        ),
        SocialChannel(
            name = "Telegram",
            description = "Fastest PDF notifications, answer keys, & direct application links.",
            brandColor = Color(0xFF229ED9),
            subscribers = "Join Now",
            handle = "t.me/sewayojan_alerts",
            url = "https://whatsapp.com/channel/0029Vb7mSRl6xCSTsZCzb60Y",
            logoType = LogoType.TELEGRAM
        ),
        SocialChannel(
            name = "Facebook",
            description = "Official Facebook page for news updates, notifications & candidate discussion.",
            brandColor = Color(0xFF1877F2),
            subscribers = "Follow Page Now",
            handle = "@sarkarisewayojan",
            url = "https://www.facebook.com/sarkarisewayojan",
            logoType = LogoType.FACEBOOK
        ),
        SocialChannel(
            name = "LinkedIn",
            description = "Professional career opportunities, UP Private/Outsourcing jobs & networking.",
            brandColor = Color(0xFF0A66C2),
            subscribers = "Follow Now",
            handle = "Sewayojan Uttar Pradesh",
            url = "https://whatsapp.com/channel/0029Vb7mSRl6xCSTsZCzb60Y",
            logoType = LogoType.LINKEDIN
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Header Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Join Sewayojan Community",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Stay updated with official 2026 Govt Job notifications, admit cards, and results instantly on your favorite social platforms.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onShowToast("Joining WhatsApp Official Channel...")
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029Vb7mSRl6xCSTsZCzb60Y"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowToast("Opening WhatsApp Web...")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Text(
                            text = "Join WhatsApp",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onShowToast("Sharing App APK...")
                            try {
                                val appInfo = context.applicationContext.applicationInfo
                                val apkFile = java.io.File(appInfo.sourceDir)
                                val appLink = "https://www.sarkarisewayojan.com"
                                val shareText = "Install Sewayojan Govt Jobs Official App for Latest Govt Jobs, Admit Cards & Results!\nWebsite: $appLink"

                                if (apkFile.exists()) {
                                    val apkUri: Uri = androidx.core.content.FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        apkFile
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/vnd.android.package-archive"
                                        putExtra(Intent.EXTRA_STREAM, apkUri)
                                        putExtra(Intent.EXTRA_SUBJECT, "Sewayojan Govt Jobs App 2026")
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    val chooserIntent = Intent.createChooser(shareIntent, "Share Sewayojan App APK via").apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(chooserIntent)
                                } else {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Sewayojan Govt Jobs App 2026")
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    val chooserIntent = Intent.createChooser(shareIntent, "Share Sewayojan App via").apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(chooserIntent)
                                }
                            } catch (e: Exception) {
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Sewayojan Govt Jobs App 2026")
                                        putExtra(Intent.EXTRA_TEXT, "Download Sewayojan Govt Jobs Official App for Latest Govt Jobs, Admit Cards & Results:\nhttps://www.sarkarisewayojan.com")
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    val chooserIntent = Intent.createChooser(shareIntent, "Share Sewayojan App via").apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(chooserIntent)
                                } catch (ex: Exception) {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Sewayojan App Link", "https://www.sarkarisewayojan.com")
                                    clipboard?.setPrimaryClip(clip)
                                    onShowToast("App link copied to clipboard!")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share App",
                            style = MaterialTheme.typography.labelLarge.copy(color = PrimaryRed, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Official Handles & Pages",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        channels.forEach { channel ->
            SocialChannelCard(
                channel = channel,
                onConnectClick = {
                    onShowToast("Opening ${channel.name}...")
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(channel.url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        onShowToast("Could not launch URL: ${channel.url}")
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

/**
 * High-end Glassy Shimmer Badge with animated curved light-sweep ("Chamak") effect.
 */
@Composable
fun ShimmerGlossyBadge(
    modifier: Modifier = Modifier,
    brandColor: Color,
    gradientColors: List<Color>? = null,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_transition")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3200
                -1.5f at 0 with FastOutSlowInEasing
                2.5f at 1400
                2.5f at 3200 // Offscreen quiet rest period for smooth non-jerk loop
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (gradientColors != null) {
                    Modifier.background(Brush.linearGradient(gradientColors))
                } else {
                    Modifier.background(brandColor)
                }
            )
            .drawWithContent {
                // 1. Render base brand logo vector
                drawContent()

                val w = size.width
                val h = size.height

                // 2. Glossy top lens rim specular highlight
                val rimPath = Path().apply {
                    addArc(
                        oval = Rect(w * 0.05f, h * 0.05f, w * 0.95f, h * 0.95f),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 180f
                    )
                }
                drawPath(
                    path = rimPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = w * 0.05f)
                )

                // 3. Realistic Curved "Chamak" Light Sweep Streak across smooth surface
                val beamCenter = w * shimmerProgress
                val beamWidth = w * 0.28f

                val beamBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.75f), // Crisp bright glare line
                        Color.White.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    start = Offset(beamCenter - beamWidth, -h * 0.2f),
                    end = Offset(beamCenter + beamWidth, h * 1.2f)
                )

                drawRect(
                    brush = beamBrush,
                    blendMode = BlendMode.SrcOver
                )
            }
    ) {
        content()
    }
}

@Composable
fun SocialChannelCard(
    channel: SocialChannel,
    onConnectClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onConnectClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Original Logo Icon with Realistic Glossy Shimmer Light Animation
            ShimmerGlossyBadge(
                brandColor = channel.brandColor,
                gradientColors = channel.gradientColors,
                modifier = Modifier.size(54.dp)
            ) {
                SocialBrandLogo(logoType = channel.logoType)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = channel.brandColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = channel.subscribers,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = channel.brandColor,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = channel.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = channel.brandColor.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Connect",
                        tint = channel.brandColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Precision Vector Drawings for Official Brand Logos
 */
@Composable
fun SocialBrandLogo(logoType: LogoType) {
    if (logoType == LogoType.WHATSAPP) {
        Image(
            painter = painterResource(id = R.drawable.ic_whatsapp_official),
            contentDescription = "WhatsApp Official Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Canvas(modifier = Modifier.size(32.dp)) {
            val w = size.width
            val h = size.height

            when (logoType) {
                LogoType.WHATSAPP -> {}
                LogoType.INSTAGRAM -> {
                val strokeWidth = 2.8.dp.toPx()

                // Outer Rounded Squircle Camera Frame
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(w * 0.16f, h * 0.16f),
                    size = Size(w * 0.68f, h * 0.68f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.20f),
                    style = Stroke(width = strokeWidth)
                )

                // Center Camera Lens
                drawCircle(
                    color = Color.White,
                    radius = w * 0.17f,
                    center = Offset(w * 0.50f, h * 0.50f),
                    style = Stroke(width = strokeWidth)
                )

                // Top-Right Flash Indicator Dot
                drawCircle(
                    color = Color.White,
                    radius = w * 0.045f,
                    center = Offset(w * 0.68f, h * 0.32f)
                )
            }

            LogoType.YOUTUBE -> {
                // White Rounded YouTube Play Card
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(w * 0.08f, h * 0.22f),
                    size = Size(w * 0.84f, h * 0.56f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.18f)
                )

                // Sharp Red Centered Play Triangle
                val trianglePath = Path().apply {
                    moveTo(w * 0.42f, h * 0.36f)
                    lineTo(w * 0.66f, h * 0.50f)
                    lineTo(w * 0.42f, h * 0.64f)
                    close()
                }
                drawPath(trianglePath, color = Color(0xFFFF0000))
            }

            LogoType.TELEGRAM -> {
                // Official Telegram Paper Plane
                val planePath = Path().apply {
                    moveTo(w * 0.18f, h * 0.48f)
                    lineTo(w * 0.82f, h * 0.22f)
                    lineTo(w * 0.68f, h * 0.78f)
                    lineTo(w * 0.48f, h * 0.62f)
                    lineTo(w * 0.38f, h * 0.72f)
                    lineTo(w * 0.36f, h * 0.56f)
                    close()
                }
                drawPath(planePath, color = Color.White)

                // Wing Fold Shadow Accent
                val foldPath = Path().apply {
                    moveTo(w * 0.48f, h * 0.62f)
                    lineTo(w * 0.38f, h * 0.72f)
                    lineTo(w * 0.42f, h * 0.54f)
                    close()
                }
                drawPath(foldPath, color = Color(0xFFD0ECFB))
            }

            LogoType.FACEBOOK -> {
                // Official Bold Off-Center Facebook 'f'
                val fPath = Path().apply {
                    moveTo(w * 0.64f, h * 0.20f)
                    cubicTo(w * 0.52f, h * 0.20f, w * 0.44f, h * 0.26f, w * 0.44f, h * 0.38f)
                    lineTo(w * 0.44f, h * 0.48f)
                    lineTo(w * 0.32f, h * 0.48f)
                    lineTo(w * 0.32f, h * 0.62f)
                    lineTo(w * 0.44f, h * 0.62f)
                    lineTo(w * 0.44f, h * 0.98f)
                    lineTo(w * 0.60f, h * 0.98f)
                    lineTo(w * 0.60f, h * 0.62f)
                    lineTo(w * 0.74f, h * 0.62f)
                    lineTo(w * 0.77f, h * 0.48f)
                    lineTo(w * 0.60f, h * 0.48f)
                    lineTo(w * 0.60f, h * 0.39f)
                    cubicTo(w * 0.60f, h * 0.35f, w * 0.64f, h * 0.33f, w * 0.70f, h * 0.33f)
                    lineTo(w * 0.77f, h * 0.33f)
                    lineTo(w * 0.77f, h * 0.20f)
                    close()
                }
                drawPath(fPath, color = Color.White)
            }

            LogoType.LINKEDIN -> {
                // Official LinkedIn 'in' Typography Logo
                val inPath = Path().apply {
                    // 'i' dot
                    val dotRadius = w * 0.07f
                    val dotCenter = Offset(w * 0.26f, h * 0.26f)
                    addOval(
                        Rect(
                            left = dotCenter.x - dotRadius,
                            top = dotCenter.y - dotRadius,
                            right = dotCenter.x + dotRadius,
                            bottom = dotCenter.y + dotRadius
                        )
                    )
                    // 'i' stem
                    addRect(Rect(w * 0.19f, h * 0.40f, w * 0.33f, h * 0.82f))

                    // 'n' left stem
                    addRect(Rect(w * 0.42f, h * 0.40f, w * 0.56f, h * 0.82f))

                    // 'n' arch & right stem
                    moveTo(w * 0.56f, h * 0.52f)
                    cubicTo(w * 0.56f, h * 0.42f, w * 0.65f, h * 0.38f, w * 0.74f, h * 0.38f)
                    cubicTo(w * 0.83f, h * 0.38f, w * 0.87f, h * 0.45f, w * 0.87f, h * 0.58f)
                    lineTo(w * 0.87f, h * 0.82f)
                    lineTo(w * 0.73f, h * 0.82f)
                    lineTo(w * 0.73f, h * 0.60f)
                    cubicTo(w * 0.73f, h * 0.54f, w * 0.69f, h * 0.52f, w * 0.65f, h * 0.52f)
                    cubicTo(w * 0.60f, h * 0.52f, w * 0.56f, h * 0.56f, w * 0.56f, h * 0.60f)
                    close()
                }
                drawPath(inPath, color = Color.White)
            }
        }
    }
}
}
