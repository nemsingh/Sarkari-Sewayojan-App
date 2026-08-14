package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import com.example.ui.theme.SurfaceContainerHigh

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun SewayojanDrawerContent(
    onNavigateTab: (Int) -> Unit,
    onCloseDrawer: () -> Unit,
    isNotificationEnabled: Boolean = true,
    onToggleNotification: () -> Unit = {},
    onOpenServiceDialog: (String) -> Unit = {},
    onOpenTool: (String) -> Unit = {},
    onShowToast: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 16.dp)
        ) {
            // Header Profile Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // App Original Logo with White Border
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        color = Color.White,
                        modifier = Modifier
                            .size(56.dp)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_icon_1785140212852),
                                contentDescription = "Sarkari Sewayojan Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(1.26f)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { onShowToast("Opening Settings") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = SecondaryGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Sarkari Sewayojan",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Helpdesk@sarkarisewayojan.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Chat With Us Helper Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryRed.copy(alpha = 0.08f))
                        .clickable {
                            onNavigateTab(3) // Ask Us AI
                            onCloseDrawer()
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Facing issues? Chat With Us",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryRed
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Chat",
                        tint = PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = SurfaceContainerHigh)

            // Scrollable Navigation Links
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                DrawerItemRow("Home", Icons.Default.Home) {
                    onNavigateTab(0)
                    onCloseDrawer()
                }
                DrawerItemRow("Latest Jobs", Icons.Default.Work) {
                    onOpenServiceDialog("Latest Jobs")
                    onCloseDrawer()
                }
                DrawerItemRow("Saved Jobs", Icons.Default.Bookmark) {
                    onOpenServiceDialog("Saved Jobs")
                    onCloseDrawer()
                }
                DrawerItemRow("Age Calculator", Icons.Default.Calculate) {
                    onOpenTool("agecalc")
                    onCloseDrawer()
                }
                DrawerItemRow(
                    title = if (isNotificationEnabled) "Notifications & Alerts (ON)" else "Notifications & Alerts (OFF)",
                    icon = if (isNotificationEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    iconTint = if (isNotificationEnabled) PrimaryRed else SecondaryGray
                ) {
                    onToggleNotification()
                    onCloseDrawer()
                }
                DrawerItemRow("OTR (One Time Registration)", Icons.Default.Badge) {
                    onOpenServiceDialog("OTR Details")
                    onCloseDrawer()
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = SurfaceContainerHigh
                )

                DrawerItemRow("Help & Support", Icons.AutoMirrored.Filled.HelpCenter) {
                    onOpenServiceDialog("Help & Support")
                    onCloseDrawer()
                }
                DrawerItemRow("About Sewayojan", Icons.Default.Info) {
                    onOpenServiceDialog("About Sewayojan")
                    onCloseDrawer()
                }
                DrawerItemRow("Privacy Policy & Disclaimer", Icons.Default.Description) {
                    onOpenServiceDialog("Privacy Policy & Disclaimer")
                    onCloseDrawer()
                }
            }

            // Footer Action: Share App
            val context = androidx.compose.ui.platform.LocalContext.current
            Box(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerHigh)
                        .clickable {
                            try {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Sarkari Sewayojan - Govt Jobs & Sarkari Result App")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Download Sarkari Sewayojan App for Latest Govt Jobs, Admit Cards, Results & Daily Alerts:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share App Via"))
                            } catch (e: Exception) {
                                onShowToast("Unable to share app link")
                            }
                            onCloseDrawer()
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share App",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share App",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerItemRow(
    title: String,
    icon: ImageVector,
    iconTint: Color = SecondaryGray,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
