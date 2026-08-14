package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JobOpportunity
import com.example.ui.theme.GreenLive
import com.example.ui.theme.GreenLiveBg
import com.example.ui.theme.SurfaceContainerHigh
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun LiveUpdatesMarquee(
    jobs: List<JobOpportunity> = emptyList(),
    jobsList: List<String> = emptyList(),
    onJobSelect: ((JobOpportunity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val displayJobs = remember(jobs, jobsList) {
        if (jobs.isNotEmpty()) {
            jobs
        } else if (jobsList.isNotEmpty()) {
            jobsList.map { title ->
                JobOpportunity(
                    title = title,
                    category = "Latest Jobs",
                    department = "Sarkari Sewayojan",
                    description = title
                )
            }
        } else {
            listOf(
                JobOpportunity(
                    title = "Bank of India Credit Officer Online Form 2026",
                    category = "Latest Jobs",
                    department = "Bank of India"
                ),
                JobOpportunity(
                    title = "UPSRLM Various Post Online Form 2026",
                    category = "Latest Jobs",
                    department = "UPSRLM"
                ),
                JobOpportunity(
                    title = "UP Police Constable Answer Key & Score Card",
                    category = "Result",
                    department = "UP Police"
                ),
                JobOpportunity(
                    title = "NTPC Assistant Chemist Trainee Online Form 2026",
                    category = "Latest Jobs",
                    department = "NTPC"
                ),
                JobOpportunity(
                    title = "Railway RRB Technician Grade I & III Recruitment 2026",
                    category = "Latest Jobs",
                    department = "Railway Recruitment Board"
                ),
                JobOpportunity(
                    title = "SBI Probationary Officers PO Prelims Exam Date 2026",
                    category = "Latest Jobs",
                    department = "State Bank of India"
                )
            )
        }
    }

    // Pulse animation for LIVE badge
    val infiniteTransition = rememberInfiniteTransition(label = "LivePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    val scrollState = rememberScrollState()

    // Continuous resilient auto-scroll loop
    LaunchedEffect(scrollState.maxValue, displayJobs) {
        if (scrollState.maxValue > 0) {
            while (coroutineContext.isActive) {
                try {
                    val remainingPx = scrollState.maxValue - scrollState.value
                    if (remainingPx > 0) {
                        val duration = (remainingPx * 22).coerceAtLeast(1200)
                        scrollState.animateScrollTo(
                            value = scrollState.maxValue,
                            animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                        )
                    }
                    delay(3000L)
                    scrollState.scrollTo(0)
                    delay(500L)
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    delay(1500L)
                }
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LIVE Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GreenLiveBg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(GreenLive.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = GreenLive
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Horizontally Auto-Scrolling Marquee (enabled = false prevents gesture intercepting taps)
        Box(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState, enabled = false)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                displayJobs.forEachIndexed { index, jobItem ->
                    Text(
                        text = jobItem.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onJobSelect?.invoke(jobItem)
                        }
                    )
                    if (index < displayJobs.size - 1) {
                        Text(
                            text = "   •   ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}
