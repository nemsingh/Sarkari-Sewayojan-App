package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.JobOpportunity
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import com.example.ui.theme.SurfaceContainerHigh

@Composable
fun JobsScreen(
    jobsList: List<JobOpportunity>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onJobSelect: (JobOpportunity) -> Unit,
    onToggleBookmark: (JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Central", "UP", "Banking", "Railway")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Filter Chips Horizontal Scroll
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryRed else SurfaceContainerHigh)
                            .clickable { onCategorySelect(category) }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Dynamic Header Banner (Airtel style)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryFixed)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Trusted Job Updates",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Since 2022 • Verified sources",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = PrimaryRed
                        )
                    }
                }
            }
        }

        // RECENT UPDATES Section Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "RECENT UPDATES",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = SecondaryGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Job Cards List
        itemsIndexed(
            items = jobsList,
            key = { index, job -> "${index}_${job.applyUrl.ifBlank { job.title }}" }
        ) { _, job ->
            JobCardItem(
                job = job,
                onCardClick = { onJobSelect(job) },
                onToggleBookmark = { onToggleBookmark(job) },
                onActionClick = {
                    if (job.statusTag == "UPCOMING") {
                        onShowToast("Notification set for ${job.title}")
                    } else {
                        onJobSelect(job)
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // View All Vacancies Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { onShowToast("Loaded 50+ additional government vacancies") },
                    shape = CircleShape,
                    modifier = Modifier
                        .height(48.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "View All Vacancies",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun JobCardItem(
    job: JobOpportunity,
    onCardClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onActionClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Organization Logo
            AsyncImage(
                model = job.logoUrl,
                contentDescription = job.department,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (job.statusTag) {
                                    "NEW" -> PrimaryRed.copy(alpha = 0.1f)
                                    "EXTENDED" -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> SurfaceContainerHigh
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = job.statusTag,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (job.statusTag) {
                                "NEW" -> PrimaryRed
                                "EXTENDED" -> SecondaryGray
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = job.dateText,
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryGray
                        )
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = PrimaryRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = job.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                val displayVacancies = remember(job.vacancies, job.dateText) {
                    val invalidValues = setOf("null", "none", "undefined", "govt vacancy", "latest", "released", "declared", "", "notice out")
                    var clean = job.vacancies.trim()
                        .replace(Regex("<[^>]*>"), "")
                        .replace(Regex("(?i)^(?:Apply\\s+Online\\s+|Registration\\s+|Online\\s+Form\\s+)?Last\\s+Date\\s*:?\\s*"), "")
                        .replace(Regex("(?i)^Last\\s*:\\s*"), "")
                        .trim()
                    if (clean.isBlank() || clean.lowercase() in invalidValues || clean.lowercase().contains("null")) {
                        clean = job.dateText.trim()
                            .replace(Regex("<[^>]*>"), "")
                            .replace(Regex("(?i)^(?:Apply\\s+Online\\s+|Registration\\s+|Online\\s+Form\\s+)?Last\\s+Date\\s*:?\\s*"), "")
                            .replace(Regex("(?i)^Last\\s*:\\s*"), "")
                            .trim()
                    }
                    if (clean.isNotBlank() && clean.lowercase() !in invalidValues && !clean.lowercase().contains("null")) "Last Date: $clean" else "Last Date: Notice Out"
                }
                Text(
                    text = displayVacancies,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryGray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = onActionClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGray),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = when (job.statusTag) {
                                "UPCOMING" -> "Notify Me"
                                "EXTENDED" -> "View Details"
                                else -> "View Details"
                            },
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
