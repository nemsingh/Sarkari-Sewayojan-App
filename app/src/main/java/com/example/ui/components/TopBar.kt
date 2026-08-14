package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import com.example.ui.theme.SurfaceContainerHigh

@Composable
fun SewayojanTopBar(
    title: String = "Sarkari Sewayojan",
    isNotificationEnabled: Boolean = true,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationToggle: () -> Unit = {},
    onQrScanClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = PrimaryRed,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onNotificationToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isNotificationEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = if (isNotificationEnabled) "Notifications On" else "Notifications Off",
                    tint = if (isNotificationEnabled) PrimaryRed else SecondaryGray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private data class FixedCategoryItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun FixedCategoryIconBar(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        FixedCategoryItem("Home", Icons.Default.Home),
        FixedCategoryItem("Latest Jobs", Icons.Default.NewReleases),
        FixedCategoryItem("Admit Card", Icons.Default.Badge),
        FixedCategoryItem("Result", Icons.Default.TaskAlt),
        FixedCategoryItem("Answer Key", Icons.Default.Checklist),
        FixedCategoryItem("Verification", Icons.Default.Verified),
        FixedCategoryItem("Admission", Icons.Default.School),
        FixedCategoryItem("Syllabus", Icons.Default.MenuBook),
        FixedCategoryItem("Important", Icons.Default.FolderSpecial),
        FixedCategoryItem("Offline Job", Icons.Default.Assignment)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(items) { item ->
            val isSelected = selectedCategory == item.title || (selectedCategory == "Home" && item.title == "Home") || (selectedCategory == "All" && item.title == "Home")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onCategorySelect(item.title)
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) PrimaryRed else SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) PrimaryRed else SecondaryGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                val indicatorWidth by animateDpAsState(
                    targetValue = if (isSelected) 20.dp else 0.dp,
                    label = "RedIndicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .height(2.5.dp)
                        .width(indicatorWidth)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) PrimaryRed else Color.Transparent)
                )
            }
        }
    }
}

