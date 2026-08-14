package com.example.ui.screens

import com.example.util.HtmlDateExtractor
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.JobOpportunity
import com.example.ui.components.LiveUpdatesMarquee
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import kotlinx.coroutines.delay

data class FilterTab(val title: String, val icon: ImageVector)
data class ServiceGridItem(val title: String, val icon: ImageVector, val isHighlight: Boolean = false)

data class HomeCategoryLists(
    val latestJobsList: List<JobOpportunity> = emptyList(),
    val admitCardList: List<JobOpportunity> = emptyList(),
    val resultList: List<JobOpportunity> = emptyList(),
    val admissionList: List<JobOpportunity> = emptyList()
)

data class TopBannerCardData(
    val title: String,
    val department: String,
    val vacancies: String,
    val tag: String = "FEATURED OPPORTUNITY",
    val buttonText: String = "Apply Now",
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val job: JobOpportunity
)

data class PhotoCardData(
    val authority: String,
    val title: String,
    val logoUrl: String,
    val imageUrl: String,
    val buttonText: String,
    val toastMessage: String,
    val job: JobOpportunity? = null,
    val categoryName: String = ""
)

fun getCategoryIconForPhotoCard(categoryName: String, title: String): ImageVector {
    val lower = "$categoryName $title".lowercase()
    return when {
        lower.contains("admit") -> Icons.Default.Badge
        lower.contains("result") -> Icons.Default.Verified
        lower.contains("answer") -> Icons.Default.Checklist
        lower.contains("admission") -> Icons.Default.School
        lower.contains("important") -> Icons.Default.MilitaryTech
        lower.contains("syllabus") -> Icons.Default.MenuBook
        lower.contains("offline") -> Icons.Default.Assignment
        else -> Icons.Default.NewReleases
    }
}

fun getPhotoBannersHelper(allJobs: List<JobOpportunity>): List<PhotoCardData> {
    val categoriesToExclude = setOf("latest jobs", "latest job", "latest_jobs", "latest_job")

    fun getButtonTextForCategory(cat: String): String {
        val lower = cat.lowercase().trim()
        return when {
            lower.contains("admit") -> "Download Admit Card"
            lower.contains("result") -> "View Result"
            lower.contains("answer") -> "Check Answer Key"
            lower.contains("admission") -> "Open Admission"
            lower.contains("important") -> "Open Details"
            else -> "Open Details"
        }
    }

    fun getCategoryImageUrl(cat: String): String {
        val lower = cat.lowercase()
        return when {
            lower.contains("admit") -> "https://images.unsplash.com/photo-1521791136064-7986c2920216?auto=format&fit=crop&w=800&q=80"
            lower.contains("result") -> "https://lh3.googleusercontent.com/aida-public/AB6AXuCoTHNWTzBvUHB7I-eickN9qrzC4rAgg-pZRkDl4R4W7ByxU-xs_YxlDsuTDNvTCgNNXfMeawOLY23jcRw3nVMPj8-GIz6mhW1dmMJYtWfC9_sSpvp36W10BNkk-WufUnbraifeRwS-hBRXu_1DBDTcgOGKw5RJYT7WqbvVJPBeEQGJOi52l68Ig22yfeynkHOmkMWHp9vzHrEQsydgI8kM403HnYzuQC56inydWAjGfhRtMhEIM7whw73h4r3AkuWWstdoq7O4mRqD"
            lower.contains("answer") -> "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=800&q=80"
            lower.contains("admission") -> "https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=800&q=80"
            lower.contains("important") -> "https://images.unsplash.com/photo-1541872703-74c5e44368f9?auto=format&fit=crop&w=800&q=80"
            else -> "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=800&q=80"
        }
    }

    val targetCategories = listOf("Admit Card", "Result", "Answer Key", "Admission", "Important", "Syllabus", "Verification", "Offline Form")
    val list = mutableListOf<PhotoCardData>()
    val processedCategories = mutableSetOf<String>()

    if (allJobs.isNotEmpty()) {
        for (targetCat in targetCategories) {
            val matchedJob = allJobs.firstOrNull { job ->
                val c = job.category.lowercase().trim()
                c == targetCat.lowercase() || c.contains(targetCat.lowercase())
            }
            if (matchedJob != null) {
                processedCategories.add(matchedJob.category.lowercase().trim())
                list.add(
                    PhotoCardData(
                        authority = matchedJob.department.ifBlank { targetCat.uppercase() },
                        title = matchedJob.title,
                        logoUrl = matchedJob.logoUrl.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuBEcPrZByS_l3xr5sBty91I9s694jVtb4AVTALZDJ7L1Zd86b_LVSsYoKvFFIG0bnfihvTmfQpUknxTB6ZWpjMxYS1y-oG1yyfyvuRmx2nlXB_n-wJFW6JdPw0_zDqIqIcc9Qli5BWR9tyG527ROeUGy_ooMNeoknLiEFhrTpeCXIeOKmlsf6QREdfrsUlv0wxIfWBc1JfwEP9Bf4SlZXvOIz6ytfgM1BigpUWmjAsFZz3JtbIRAnnq6L-9v-RYPT-ZSlmGm22oQN1F" },
                        imageUrl = getCategoryImageUrl(targetCat),
                        buttonText = getButtonTextForCategory(targetCat),
                        toastMessage = "Sharing ${matchedJob.title}",
                        job = matchedJob,
                        categoryName = matchedJob.category.ifBlank { targetCat }
                    )
                )
            }
        }

        val otherJobs = allJobs.filter { job ->
            val c = job.category.lowercase().trim()
            c !in categoriesToExclude && c !in processedCategories && targetCategories.none { tc -> c.contains(tc.lowercase()) }
        }
        val groupedByOtherCat = otherJobs.groupBy { it.category.lowercase().trim() }
        for ((catName, jobsInCat) in groupedByOtherCat) {
            val topJob = jobsInCat.firstOrNull()
            if (topJob != null) {
                list.add(
                    PhotoCardData(
                        authority = topJob.department.ifBlank { topJob.category.uppercase() },
                        title = topJob.title,
                        logoUrl = topJob.logoUrl.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuBEcPrZByS_l3xr5sBty91I9s694jVtb4AVTALZDJ7L1Zd86b_LVSsYoKvFFIG0bnfihvTmfQpUknxTB6ZWpjMxYS1y-oG1yyfyvuRmx2nlXB_n-wJFW6JdPw0_zDqIqIcc9Qli5BWR9tyG527ROeUGy_ooMNeoknLiEFhrTpeCXIeOKmlsf6QREdfrsUlv0wxIfWBc1JfwEP9Bf4SlZXvOIz6ytfgM1BigpUWmjAsFZz3JtbIRAnnq6L-9v-RYPT-ZSlmGm22oQN1F" },
                        imageUrl = getCategoryImageUrl(catName),
                        buttonText = getButtonTextForCategory(catName),
                        toastMessage = "Sharing ${topJob.title}",
                        job = topJob,
                        categoryName = topJob.category.ifBlank { catName }
                    )
                )
            }
        }
    }

    if (list.isEmpty()) {
        list.addAll(
            listOf(
                PhotoCardData(
                    authority = "STAFF SELECTION COMMISSION",
                    title = "SSC GD Constable Admit Card Released!",
                    logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCkp-f1T-zCQOD54mITMjyT4Gbxgl23brl1_kDt5L4yjvAWNoxqlBXwJqgs3MAx9oXF68ZXMTBhoxru8k2Zj3GsvOUXWZK2yU2i4nH06poCg7Xrut-AVaF1Obdpbl0eWZeQ1pG_TQnJMiq4ZHqJ2brKr6WwxzJkPYIROv_xZf-t5HGlemo7Gbf4Ou9JbIs6thC8vfQj-lptPQ3ywv5s2LbvBktRHS3PNW5mpyoGqP2G1-c43CIue3g4DNaEheA4A2HLhDPPDTb4wcGG",
                    imageUrl = getCategoryImageUrl("Admit Card"),
                    buttonText = getButtonTextForCategory("Admit Card"),
                    toastMessage = "Opening SSC GD Constable Admit Card Portal",
                    categoryName = "Admit Card"
                ),
                PhotoCardData(
                    authority = "UTTAR PRADESH POLICE",
                    title = "UP Police Constable Result Out!",
                    logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBEcPrZByS_l3xr5sBty91I9s694jVtb4AVTALZDJ7L1Zd86b_LVSsYoKvFFIG0bnfihvTmfQpUknxTB6ZWpjMxYS1y-oG1yyfyvuRmx2nlXB_n-wJFW6JdPw0_zDqIqIcc9Qli5BWR9tyG527ROeUGy_ooMNeoknLiEFhrTpeCXIeOKmlsf6QREdfrsUlv0wxIfWBc1JfwEP9Bf4SlZXvOIz6ytfgM1BigpUWmjAsFZz3JtbIRAnnq6L-9v-RYPT-ZSlmGm22oQN1F",
                    imageUrl = getCategoryImageUrl("Result"),
                    buttonText = getButtonTextForCategory("Result"),
                    toastMessage = "Sharing UP Police Constable Result Link",
                    categoryName = "Result"
                ),
                PhotoCardData(
                    authority = "NATIONAL TESTING AGENCY",
                    title = "NTA JEE Main Answer Key Out",
                    logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBEcPrZByS_l3xr5sBty91I9s694jVtb4AVTALZDJ7L1Zd86b_LVSsYoKvFFIG0bnfihvTmfQpUknxTB6ZWpjMxYS1y-oG1yyfyvuRmx2nlXB_n-wJFW6JdPw0_zDqIqIcc9Qli5BWR9tyG527ROeUGy_ooMNeoknLiEFhrTpeCXIeOKmlsf6QREdfrsUlv0wxIfWBc1JfwEP9Bf4SlZXvOIz6ytfgM1BigpUWmjAsFZz3JtbIRAnnq6L-9v-RYPT-ZSlmGm22oQN1F",
                    imageUrl = getCategoryImageUrl("Answer Key"),
                    buttonText = getButtonTextForCategory("Answer Key"),
                    toastMessage = "Opening NTA JEE Main Portal",
                    categoryName = "Answer Key"
                ),
                PhotoCardData(
                    authority = "INDIAN ARMY RECRUITMENT",
                    title = "Indian Army Agniveer Admission 2026",
                    logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBEcPrZByS_l3xr5sBty91I9s694jVtb4AVTALZDJ7L1Zd86b_LVSsYoKvFFIG0bnfihvTmfQpUknxTB6ZWpjMxYS1y-oG1yyfyvuRmx2nlXB_n-wJFW6JdPw0_zDqIqIcc9Qli5BWR9tyG527ROeUGy_ooMNeoknLiEFhrTpeCXIeOKmlsf6QREdfrsUlv0wxIfWBc1JfwEP9Bf4SlZXvOIz6ytfgM1BigpUWmjAsFZz3JtbIRAnnq6L-9v-RYPT-ZSlmGm22oQN1F",
                    imageUrl = getCategoryImageUrl("Admission"),
                    buttonText = getButtonTextForCategory("Admission"),
                    toastMessage = "Opening Army Agniveer Notification",
                    categoryName = "Admission"
                ),
                PhotoCardData(
                    authority = "UNION PUBLIC SERVICE COMMISSION",
                    title = "UPSC Civil Services Important Notice",
                    logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCkp-f1T-zCQOD54mITMjyT4Gbxgl23brl1_kDt5L4yjvAWNoxqlBXwJqgs3MAx9oXF68ZXMTBhoxru8k2Zj3GsvOUXWZK2yU2i4nH06poCg7Xrut-AVaF1Obdpbl0eWZeQ1pG_TQnJMiq4ZHqJ2brKr6WwxzJkPYIROv_xZf-t5HGlemo7Gbf4Ou9JbIs6thC8vfQj-lptPQ3ywv5s2LbvBktRHS3PNW5mpyoGqP2G1-c43CIue3g4DNaEheA4A2HLhDPPDTb4wcGG",
                    imageUrl = getCategoryImageUrl("Important"),
                    buttonText = getButtonTextForCategory("Important"),
                    toastMessage = "Opening UPSC Merit List PDF",
                    categoryName = "Important"
                )
            )
        )
    }

    return list
}

fun getTrendingJobsHelper(allJobs: List<JobOpportunity>): List<JobOpportunity> {
    val rawLatest = allJobs.filter { it.category.contains("Jobs", ignoreCase = true) || it.category == "Latest Jobs" }
    val fullLatest = if (rawLatest.size >= 7) {
        rawLatest
    } else {
        val list = rawLatest.toMutableList()
        for (item in allJobs) {
            if (!list.any { it.title == item.title }) {
                list.add(item.copy(category = "Latest Jobs"))
            }
        }
        list
    }
    return if (fullLatest.size > 5) {
        fullLatest.subList(5, (7).coerceAtMost(fullLatest.size))
    } else if (fullLatest.size >= 2) {
        fullLatest.take(2)
    } else {
        listOf(
            JobOpportunity(
                title = "SBI PO Form 1500+ Posts",
                category = "Latest Jobs",
                department = "State Bank of India",
                dateText = "15 Aug 2026"
            ),
            JobOpportunity(
                title = "Airforce Agniveer Intake 01/2027",
                category = "Latest Jobs",
                department = "Indian Air Force",
                dateText = "Active Now"
            )
        )
    }
}

@Composable
fun HomeScreen(
    selectedCategory: String,
    searchQuery: String,
    allJobs: List<JobOpportunity> = emptyList(),
    admitCards: List<com.example.data.local.AdmitCardItem> = emptyList(),
    examResults: List<com.example.data.local.ExamResultItem> = emptyList(),
    topBanners: List<TopBannerCardData> = emptyList(),
    photoBanners: List<PhotoCardData> = emptyList(),
    trendingJobs: List<JobOpportunity> = emptyList(),
    homeCategoryLists: HomeCategoryLists = HomeCategoryLists(),
    onCategorySelect: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTriggerSearch: (String) -> Unit = {},
    onJobSelect: (JobOpportunity) -> Unit,
    onNavigateTab: (Int) -> Unit,
    onOpenServiceDialog: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val actualTopBanners = remember(topBanners, allJobs) {
        if (topBanners.isNotEmpty()) topBanners else getUrgentTopBanners(allJobs)
    }

    val topPagerState = rememberPagerState(pageCount = { actualTopBanners.size })
    LaunchedEffect(actualTopBanners.size) {
        if (actualTopBanners.size > 1) {
            while (coroutineContext.isActive) {
                try {
                    delay(5000L)
                    val count = topPagerState.pageCount
                    if (count > 1 && !topPagerState.isScrollInProgress) {
                        val current = topPagerState.currentPage
                        if (current >= count) {
                            topPagerState.scrollToPage(0)
                        } else {
                            val nextPage = (current + 1) % count
                            topPagerState.animateScrollToPage(nextPage)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    // Prevent any crash during rapid interactions or layout updates
                }
            }
        }
    }

    val actualPhotoBanners = remember(photoBanners, allJobs) {
        if (photoBanners.isNotEmpty()) photoBanners else getPhotoBannersHelper(allJobs)
    }

    val photoPagerState = rememberPagerState(pageCount = { actualPhotoBanners.size })

    val actualTrendingJobs = remember(trendingJobs, allJobs) {
        if (trendingJobs.isNotEmpty()) trendingJobs else getTrendingJobsHelper(allJobs)
    }
    LaunchedEffect(actualPhotoBanners.size) {
        if (actualPhotoBanners.size > 1) {
            while (coroutineContext.isActive) {
                try {
                    delay(4000L)
                    val count = photoPagerState.pageCount
                    if (count > 1 && !photoPagerState.isScrollInProgress) {
                        val current = photoPagerState.currentPage
                        if (current >= count) {
                            photoPagerState.scrollToPage(0)
                        } else {
                            val nextPage = (current + 1) % count
                            photoPagerState.animateScrollToPage(nextPage)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    // Prevent any crash during rapid interactions or layout updates
                }
            }
        }
    }

    val serviceGrid = listOf(
        ServiceGridItem("Latest Jobs", Icons.Default.NewReleases, isHighlight = true),
        ServiceGridItem("Admit Card", Icons.Default.Badge),
        ServiceGridItem("Result", Icons.Default.TaskAlt),
        ServiceGridItem("Answer Key", Icons.Default.Checklist),
        ServiceGridItem("Verification", Icons.Default.Verified),
        ServiceGridItem("Admission", Icons.Default.School),
        ServiceGridItem("Syllabus", Icons.Default.MenuBook),
        ServiceGridItem("Important", Icons.Default.FolderSpecial),
        ServiceGridItem("Offline Job", Icons.Default.Assignment)
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // padding for floating bottom bar
        ) {
            Spacer(modifier = Modifier.height(8.dp))

        // Hero Section: Auto-sliding 8-card Featured Opportunity Carousel
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = topPagerState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 12.dp
            ) { page ->
                val banner = topBanners.getOrNull(page) ?: return@HorizontalPager
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp) // Uniform fixed height across all 8 cards
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(colors = banner.gradientColors)
                        )
                        .clickable { onJobSelect(banner.job) }
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Pill Tag & Department
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = banner.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }

                            Text(
                                text = banner.department,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Middle: Short Professional Title & Clean 1-Line Subtitle
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = banner.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    lineHeight = 24.sp
                                ),
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = banner.vacancies,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Bottom Row: Apply Now Button & Icon Box (Latest Category Icon: Icons.Default.NewReleases)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onJobSelect(banner.job)
                                },
                                shape = CircleShape,
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = banner.buttonText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryRed
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NewReleases, // Latest Category Icon for ALL cards
                                    contentDescription = "Latest Job",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Indicator Dots for Top Carousel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(topBanners.size) { index ->
                    val isSelected = topPagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 22.dp else 6.dp,
                        label = "TopDotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) PrimaryRed else PrimaryRed.copy(alpha = 0.25f)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Category Boxes Grid Section (Latest Job, Admit Card, Result, Admission)
        FourCategoryGridSection(
            allJobs = allJobs,
            admitCards = admitCards,
            examResults = examResults,
            homeCategoryLists = homeCategoryLists,
            onOpenServiceDialog = onOpenServiceDialog,
            onJobSelect = onJobSelect
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Search Bar Integrated
        var homeDraftSearch by remember(searchQuery) { mutableStateOf(searchQuery) }
        val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            BasicTextField(
                value = homeDraftSearch,
                onValueChange = { homeDraftSearch = it },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = {
                        val trimmed = homeDraftSearch.trim()
                        if (trimmed.isNotBlank()) {
                            onSearchQueryChange(trimmed)
                            onTriggerSearch(trimmed)
                            keyboardController?.hide()
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(SurfaceContainerLow, CircleShape)
                            .border(
                                width = 1.dp,
                                color = PrimaryRed.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .padding(start = 16.dp, end = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (homeDraftSearch.isEmpty()) {
                                Text(
                                    text = "Type job or post name to search...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            innerTextField()
                        }
                        Surface(
                            shape = CircleShape,
                            color = PrimaryRed,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    val trimmed = homeDraftSearch.trim()
                                    if (trimmed.isNotBlank()) {
                                        onSearchQueryChange(trimmed)
                                        onTriggerSearch(trimmed)
                                        keyboardController?.hide()
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Lens",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Manage Services Grid Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sewayojan Services",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryRed
                ),
                modifier = Modifier.clickable { onNavigateTab(1) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3x3 Category Grid (3D Speaker Cone Elevated Round Buttons)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val rows = serviceGrid.chunked(3)
            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (item in row) {
                        Speaker3DIconButton(
                            icon = item.icon,
                            title = item.title,
                            isHighlight = item.isHighlight,
                            onClick = { onOpenServiceDialog(item.title) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Exam Updates Section (5-card Auto-sliding Photo Carousel)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Exam Updates",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = SecondaryGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalPager(
                state = photoPagerState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 12.dp
            ) { page ->
                val card = photoBanners.getOrNull(page) ?: return@HorizontalPager
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clickable {
                            if (card.job != null) {
                                onJobSelect(card.job)
                            } else {
                                onNavigateTab(2)
                            }
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = card.imageUrl,
                            contentDescription = card.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Dark Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                                    )
                                )
                        )

                        // Content on top
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIconForPhotoCard(card.categoryName, card.title),
                                        contentDescription = card.categoryName,
                                        tint = PrimaryRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = card.authority,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = card.title,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (card.job != null) {
                                            onJobSelect(card.job)
                                        } else {
                                            onNavigateTab(2)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text(
                                        text = card.buttonText,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val shareTitle = card.title
                                        val shareLink = if (card.job != null && card.job.applyUrl.isNotBlank()) {
                                            val url = if (card.job.applyUrl.startsWith("http")) card.job.applyUrl else "https://sewayojan.up.nic.in${card.job.applyUrl}"
                                            "\n\nDetails / Apply Link: $url"
                                        } else ""
                                        
                                        val shareText = "Sarkari Sewayojan Update:\n\n$shareTitle$shareLink\n\nDownload Sarkari Sewayojan App for more updates!"
                                        
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share Job Update").apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        try {
                                            context.startActivity(shareIntent)
                                        } catch (e: Exception) {
                                            onShowToast(card.toastMessage)
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Indicator Dots for Photo Carousel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(photoBanners.size) { index ->
                    val isSelected = photoPagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 22.dp else 6.dp,
                        label = "PhotoDotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) PrimaryRed else PrimaryRed.copy(alpha = 0.25f)
                            )
                    )
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            // Bento Grid for Trending items (6th and 7th job from Latest Jobs category)
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "TRENDING",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = SecondaryGray,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    trendingJobs.forEach { job ->
                        val extractedTag = extractLastDateTag(job, if (job.dateText.isNotBlank()) job.dateText else "Active Now")
                        val lastDateDisplay = if (extractedTag.uppercase().startsWith("LAST DATE")) extractedTag else "Last Date: $extractedTag"

                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onJobSelect(job) }
                                .padding(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PrimaryRed.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NewReleases,
                                        contentDescription = "Latest Job",
                                        tint = PrimaryRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = job.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = lastDateDisplay,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimaryRed,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Updates Strip
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            val marqueeJobs = remember(allJobs) {
                if (allJobs.isNotEmpty()) allJobs.take(10) else emptyList()
            }
            LiveUpdatesMarquee(
                jobs = marqueeJobs,
                onJobSelect = onJobSelect
            )
        }
    }

    if (allJobs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1C1C1C).copy(alpha = 0.78f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                shadowElevation = 0.dp,
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = PrimaryRed,
                        strokeWidth = 3.5.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Loading, please wait...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
}

@Composable
fun Speaker3DIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isHighlight: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        // Outer 3D Elevated Round Surface (Background se ubhra hua button)
        Surface(
            shape = CircleShape,
            shadowElevation = 8.dp,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.5.dp,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        if (isHighlight) PrimaryRed.copy(alpha = 0.5f) else Color(0xFFD6E0EA),
                        if (isHighlight) PrimaryRed.copy(alpha = 0.2f) else Color(0xFFB0BEC5)
                    )
                )
            ),
            modifier = Modifier.size(64.dp)
        ) {
            // Smooth Concave Recessed Pit (Smooth Gadda curve inward with soft inner shadow)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerPt = Offset(size.width / 2f, size.height / 2f)
                    val radiusPx = size.width / 2f

                    // 1. Smooth inward gradient (darker inner shadow wall, soft light center basin)
                    drawCircle(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                // Center basin bottom (light & clear)
                                if (isHighlight) Color(0xFFFFF5F5) else Color(0xFFF1F5F9),
                                // Smooth slope of concave pit
                                if (isHighlight) Color(0xFFFFE0E0) else Color(0xFFE2E8F0),
                                // Deepest inner shadow at the rim wall sliding inward
                                if (isHighlight) PrimaryRed.copy(alpha = 0.22f) else Color(0xFF94A3B8).copy(alpha = 0.45f)
                            ),
                            center = centerPt,
                            radius = radiusPx
                        ),
                        radius = radiusPx,
                        center = centerPt
                    )

                    // 2. Soft inner rim shadow overlay for authentic concave depth
                    drawCircle(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.12f)
                            ),
                            center = centerPt,
                            radius = radiusPx
                        ),
                        radius = radiusPx,
                        center = centerPt
                    )

                    // 3. Subtle inner lip stroke highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f),
                        radius = radiusPx - 1.5.dp.toPx(),
                        center = centerPt,
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }

                // Center Icon sitting inside the smooth indented basin
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FourCategoryGridSection(
    allJobs: List<JobOpportunity> = emptyList(),
    admitCards: List<com.example.data.local.AdmitCardItem> = emptyList(),
    examResults: List<com.example.data.local.ExamResultItem> = emptyList(),
    homeCategoryLists: HomeCategoryLists = HomeCategoryLists(),
    onOpenServiceDialog: (String) -> Unit,
    onJobSelect: (JobOpportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    val latestJobsList = remember(homeCategoryLists.latestJobsList, allJobs) {
        if (homeCategoryLists.latestJobsList.isNotEmpty()) {
            homeCategoryLists.latestJobsList
        } else {
            val jobs = allJobs.filter { it.category.contains("Jobs", ignoreCase = true) || it.category == "Latest Jobs" }
            ensureFiveCategoryJobs(jobs, allJobs, "Latest Jobs")
        }
    }

    val admitCardList = remember(homeCategoryLists.admitCardList, allJobs, admitCards) {
        if (homeCategoryLists.admitCardList.isNotEmpty()) {
            homeCategoryLists.admitCardList
        } else {
            val jobs = allJobs.filter { it.category.contains("Admit", ignoreCase = true) }
            ensureFiveCategoryJobs(jobs, allJobs, "Admit Card")
        }
    }

    val resultList = remember(homeCategoryLists.resultList, allJobs, examResults) {
        if (homeCategoryLists.resultList.isNotEmpty()) {
            homeCategoryLists.resultList
        } else {
            val jobs = allJobs.filter { it.category.contains("Result", ignoreCase = true) }
            ensureFiveCategoryJobs(jobs, allJobs, "Result")
        }
    }

    val admissionList = remember(homeCategoryLists.admissionList, allJobs) {
        if (homeCategoryLists.admissionList.isNotEmpty()) {
            homeCategoryLists.admissionList
        } else {
            val jobs = allJobs.filter { it.category.contains("Admission", ignoreCase = true) || it.category.contains("Addmission", ignoreCase = true) }
            ensureFiveCategoryJobs(jobs, allJobs, "Admission")
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {
        // First Row: Latest Job & Admit Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CategoryJobBox(
                heading = "Latest Job",
                dialogCategory = "Latest Jobs",
                jobs = latestJobsList,
                onOpenCategory = onOpenServiceDialog,
                onJobSelect = onJobSelect,
                modifier = Modifier.weight(1f)
            )
            CategoryJobBox(
                heading = "Admit Card",
                dialogCategory = "Admit Card",
                jobs = admitCardList,
                onOpenCategory = onOpenServiceDialog,
                onJobSelect = onJobSelect,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Second Row: Result & Admission
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CategoryJobBox(
                heading = "Result",
                dialogCategory = "Results",
                jobs = resultList,
                onOpenCategory = onOpenServiceDialog,
                onJobSelect = onJobSelect,
                modifier = Modifier.weight(1f)
            )
            CategoryJobBox(
                heading = "Admission",
                dialogCategory = "Admission",
                jobs = admissionList,
                onOpenCategory = onOpenServiceDialog,
                onJobSelect = onJobSelect,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

fun getHomeCategoryListsHelper(
    allJobs: List<JobOpportunity>,
    admitCards: List<com.example.data.local.AdmitCardItem> = emptyList(),
    examResults: List<com.example.data.local.ExamResultItem> = emptyList()
): HomeCategoryLists {
    val latest = allJobs.filter { it.category.contains("Jobs", ignoreCase = true) || it.category == "Latest Jobs" }
    val admits = allJobs.filter { it.category.contains("Admit", ignoreCase = true) }
    val results = allJobs.filter { it.category.contains("Result", ignoreCase = true) }
    val admissions = allJobs.filter { it.category.contains("Admission", ignoreCase = true) || it.category.contains("Addmission", ignoreCase = true) }

    return HomeCategoryLists(
        latestJobsList = ensureFiveCategoryJobs(latest, allJobs, "Latest Jobs"),
        admitCardList = ensureFiveCategoryJobs(admits, allJobs, "Admit Card"),
        resultList = ensureFiveCategoryJobs(results, allJobs, "Result"),
        admissionList = ensureFiveCategoryJobs(admissions, allJobs, "Admission")
    )
}

private fun ensureFiveCategoryJobs(
    filtered: List<JobOpportunity>,
    fallbackSource: List<JobOpportunity>,
    categoryName: String
): List<JobOpportunity> {
    val list = filtered.toMutableList()
    if (list.size < 5) {
        for (item in fallbackSource) {
            if (list.size >= 5) break
            if (!list.any { it.title == item.title }) {
                list.add(item.copy(category = categoryName))
            }
        }
    }
    return list.take(5)
}

@Composable
private fun CategoryJobBox(
    heading: String,
    dialogCategory: String,
    jobs: List<JobOpportunity>,
    onOpenCategory: (String) -> Unit,
    onJobSelect: (JobOpportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PrimaryRed)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Heading Bar with Red Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryRed)
                    .clickable { onOpenCategory(dialogCategory) }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = heading,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // List of 5 Job Titles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp, horizontal = 3.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                jobs.take(5).forEachIndexed { index, jobItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onJobSelect(jobItem) }
                            .padding(vertical = 3.5.dp, horizontal = 1.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            color = PrimaryRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Text(
                            text = jobItem.title,
                            color = Color(0xFF0000EF), // Exact Link Blue #0000EF
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (index < jobs.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFFF2F2F2),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                }
            }

            // View More Option at Bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCategory(dialogCategory) }
                    .background(Color(0xFFFFF0F0))
                    .padding(vertical = 5.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View More »",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed,
                        fontSize = 11.5.sp
                    )
                )
            }
        }
    }
}

fun getUrgentTopBanners(allJobs: List<JobOpportunity>): List<TopBannerCardData> {
    val gradients = listOf(
        listOf(PrimaryRed, Color(0xFF800000)),
        listOf(Color(0xFFB91C1C), Color(0xFF680707)),
        listOf(Color(0xFFC2410C), Color(0xFF7C2D12)),
        listOf(Color(0xFF991B1B), Color(0xFF450A0A)),
        listOf(Color(0xFFDC2626), Color(0xFF881337)),
        listOf(Color(0xFF9F1239), Color(0xFF4C0519)),
        listOf(Color(0xFFBE123C), Color(0xFF700720)),
        listOf(Color(0xFFB91C1C), Color(0xFF581C87))
    )

    if (allJobs.isEmpty()) return emptyList()

    val candidateJobs = mutableListOf<JobOpportunity>()
    val seenKeys = mutableSetOf<String>()

    // 1. Strictly gather ONLY jobs belonging to Latest Jobs category (no Admission, Result, Admit Card, etc.)
    val latestJobsFromAll = allJobs.filter { job ->
        val cat = job.category.lowercase().trim()
        val titleLower = job.title.lowercase()
        val isLatestCategory = (cat == "latest jobs" || cat == "latest job" || cat.contains("latest")) &&
                !cat.contains("admission") && !cat.contains("admit") && !cat.contains("result") && !cat.contains("answer") && !cat.contains("syllabus")
        val isNotAdmissionTitle = !titleLower.contains("admission")
        isLatestCategory && isNotAdmissionTitle
    }
    for (job in latestJobsFromAll) {
        val key = getUniqueJobKey(job)
        if (seenKeys.add(key)) {
            candidateJobs.add(job)
        }
    }

    // 2. Evaluate EVERY real Latest Job candidate against current date (check if expired, start phrase, & urgency score)
    val evaluatedCandidates = candidateJobs.map { job ->
        evaluateCandidate(job, "LAST DATE: CLOSING SOON")
    }

    // 3. Filter out ANY expired candidate OR start-date-only candidate strictly
    val activeCandidates = evaluatedCandidates.filter { !it.isExpired }

    // 4. Sort active candidates strictly by urgency score ascending (Today=0, Tomorrow=1, Closest Future Date=2..)
    val sortedCandidates = activeCandidates.sortedBy { it.score }

    // 5. Take up to 8 distinct active candidates
    val finalCandidates = sortedCandidates.distinctBy { getUniqueJobKey(it.job) }.take(8)

    return finalCandidates.mapIndexed { index, candidate ->
        val grad = gradients.getOrElse(index % gradients.size) { gradients[0] }
        val cleanTitle = shortenJobTitleForHero(candidate.job.title)
        val cleanSubtitle = buildShortSubtitle(candidate.job)

        TopBannerCardData(
            title = cleanTitle,
            department = candidate.job.department.ifBlank { "Latest Jobs Category" },
            vacancies = cleanSubtitle,
            tag = candidate.tag,
            buttonText = "Apply Now",
            icon = Icons.Default.NewReleases,
            gradientColors = grad,
            job = candidate.job
        )
    }
}

private val REGEX_HTML_TAGS = Regex("<[^>]*>")
private val REGEX_PREFIX_LAST_DATE = Regex("(?i)^(?:Apply\\s+Online\\s+|Registration\\s+|Online\\s+Form\\s+)?Last\\s+Date\\s*:?\\s*")
private val REGEX_PREFIX_LAST = Regex("(?i)^Last\\s*:\\s*")
private val REGEX_PREFIX_HINDI_DATE = Regex("(?i)^अंतिम\\s+तिथि\\s*:?\\s*|^अन्तिम\\s+तिथि\\s*:?\\s*|^अंतिम\\s+तारीख\\s*:?\\s*")
private val REGEX_DATE_MATCH = Regex("([0-9]{1,2})[/\\-\\s]([0-9A-Za-z]{1,9})[/\\-\\s](20[2-9][0-9])")
private val REGEX_TITLE_SHORTEN = Regex("(?i)recruitment|online form|apply online|notification|bharti|vacancies|vacancy|official|direct")
private val REGEX_KEY_CLEAN = Regex("(?i)202[0-9]|online form|apply online|recruitment|bharti|vacancy|vacancies|official|direct|post|posts")
private val REGEX_NON_ALPHANUM = Regex("[^a-z0-9]")
private val REGEX_SPACES = Regex("\\s+")

private val REGEX_START_DATE_PHRASES = Regex("(?i)\\b(?:link\\s+(?:will\\s+be\\s+|is\\s+)?activat(?:e|ed|ion)|link\\s+active|application\\s+begin|application\\s+start|form\\s+start|form\\s+begin|apply\\s+online\\s+start|apply\\s+start|starting\\s+date|opening\\s+date|start\\s+date|shuru|active\\s+soon|will\\s+be\\s+active|शुरू|प्रारंभ|प्रारम्भ|आरंभ|एक्टिवेट|शुरुआती)\\b")
private val REGEX_LAST_DATE_EXPLICIT = Regex("(?i)\\b(?:last\\s+date|closing\\s+date|end\\s+date|expiry\\s+date|complete\\s+form|pay\\s+fee\\s+last|अंतिम\\s+तिथि|अन्तिम\\s+तिथि|अंतिम\\s+तारीख|आखिरी\\s+तारीख|समाप्ति\\s+तिथि)\\b")

private fun isStartDatePhrase(text: String): Boolean {
    if (text.isBlank()) return false
    val hasStartKeyword = REGEX_START_DATE_PHRASES.containsMatchIn(text)
    val hasExplicitLastKeyword = REGEX_LAST_DATE_EXPLICIT.containsMatchIn(text)
    return hasStartKeyword && !hasExplicitLastKeyword
}

private data class EvaluatedCandidate(
    val job: JobOpportunity,
    val tag: String,
    val isExpired: Boolean,
    val score: Int
)

private fun evaluateCandidate(job: JobOpportunity, fallbackTag: String): EvaluatedCandidate {
    var tag = extractLastDateTag(job, fallbackTag)

    val isStartOnly = isStartDatePhrase(tag) || isStartDatePhrase(job.vacancies) || isStartDatePhrase(job.dateText)

    val expired = if (isStartOnly) {
        true // Strictly exclude start-date / link-activation items from urgent last date banners!
    } else {
        checkIsExpired(tag)
    }

    if (!expired && !isStartOnly) {
        val components = extractDateComponents(tag)
        if (components != null) {
            val (year, month, day) = components
            val cal = java.util.Calendar.getInstance()
            val curYear = cal.get(java.util.Calendar.YEAR)
            val curMonth = cal.get(java.util.Calendar.MONTH) + 1
            val curDay = cal.get(java.util.Calendar.DAY_OF_MONTH)

            val jobCal = java.util.Calendar.getInstance()
            jobCal.set(year, month - 1, day, 0, 0, 0)
            jobCal.set(java.util.Calendar.MILLISECOND, 0)

            val curCal = java.util.Calendar.getInstance()
            curCal.set(curYear, curMonth - 1, curDay, 0, 0, 0)
            curCal.set(java.util.Calendar.MILLISECOND, 0)

            val diffMs = jobCal.timeInMillis - curCal.timeInMillis
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()

            if (diffDays == 0) {
                tag = "LAST DATE: TODAY"
            } else if (diffDays == 1) {
                tag = "LAST DATE: TOMORROW"
            } else if (diffDays > 1) {
                val formattedDay = if (day < 10) "0$day" else "$day"
                val formattedMonth = if (month < 10) "0$month" else "$month"
                tag = "LAST DATE: $formattedDay/$formattedMonth/$year"
            }
        }
    }

    val score = if (isStartOnly) 99999 else calculateScore(tag)
    return EvaluatedCandidate(job, tag, expired, score)
}

private fun extractDateComponents(rawText: String): Triple<Int, Int, Int>? {
    if (rawText.isBlank()) return null
    val cal = java.util.Calendar.getInstance()
    val curYear = cal.get(java.util.Calendar.YEAR)
    val curMonth = cal.get(java.util.Calendar.MONTH) + 1

    // Strip ordinals: 31st -> 31, 1st -> 1, 2nd -> 2, 3rd -> 3, 4th -> 4
    val text = rawText.replace(Regex("(?i)(\\d+)(st|nd|rd|th)"), "$1")

    // Pattern 0: YYYY-MM-DD or YYYY/MM/DD or YYYY.MM.DD (e.g. 2026-07-31, 2026/08/15)
    val regexYYYY = Regex("(20[2-9][0-9])[/\\-\\.\\s]+([0-9]{1,2})[/\\-\\.\\s]+([0-9]{1,2})")
    val matchYYYY = regexYYYY.find(text)
    if (matchYYYY != null) {
        val year = matchYYYY.groupValues[1].toIntOrNull() ?: curYear
        val month = matchYYYY.groupValues[2].toIntOrNull() ?: 0
        val day = matchYYYY.groupValues[3].toIntOrNull() ?: 0
        if (month in 1..12 && day in 1..31) {
            return Triple(year, month, day)
        }
    }

    // Pattern 1: Day Month Year (e.g., 30/07/2026, 31-Jul-2026, 30.07.2026, 30/07/26, 30 जुलाई 2026)
    val regexFullDMY = Regex("([0-9]{1,2})[/\\-\\.\\s]+([0-9A-Za-z\\u0900-\\u097F]{1,12})[/\\-\\.\\s]+(20[2-9][0-9]|[2-9][0-9])")
    val matchFullDMY = regexFullDMY.find(text)
    if (matchFullDMY != null) {
        val day = matchFullDMY.groupValues[1].toIntOrNull() ?: return null
        val monthStr = matchFullDMY.groupValues[2]
        var year = matchFullDMY.groupValues[3].toIntOrNull() ?: curYear
        if (year < 100) year += 2000
        val month = parseMonthToNum(monthStr)
        if (month in 1..12 && day in 1..31) {
            return Triple(year, month, day)
        }
    }

    // Pattern 2: Month Day Year (e.g., July 31, 2026 or August 15 2026)
    val regexFullMDY = Regex("([A-Za-z\\u0900-\\u097F]{3,12})[/\\-\\.\\s]+([0-9]{1,2})[,/\\-\\.\\s]+(20[2-9][0-9]|[2-9][0-9])")
    val matchFullMDY = regexFullMDY.find(text)
    if (matchFullMDY != null) {
        val monthStr = matchFullMDY.groupValues[1]
        val day = matchFullMDY.groupValues[2].toIntOrNull() ?: return null
        var year = matchFullMDY.groupValues[3].toIntOrNull() ?: curYear
        if (year < 100) year += 2000
        val month = parseMonthToNum(monthStr)
        if (month in 1..12 && day in 1..31) {
            return Triple(year, month, day)
        }
    }

    // Pattern 3: Day Month without Year (e.g., 30/07, 31-Jul, 30 July, 30 जुलाई)
    val regexShortDM = Regex("([0-9]{1,2})[/\\-\\.\\s]+([0-9A-Za-z\\u0900-\\u097F]{1,12})")
    val matchShortDM = regexShortDM.find(text)
    if (matchShortDM != null) {
        val day = matchShortDM.groupValues[1].toIntOrNull() ?: return null
        val monthStr = matchShortDM.groupValues[2]
        val month = parseMonthToNum(monthStr)
        if (month in 1..12 && day in 1..31) {
            val year = if (month < curMonth - 6) curYear + 1 else curYear
            return Triple(year, month, day)
        }
    }

    return null
}

private fun extractLastDateTag(job: JobOpportunity, fallbackTag: String): String {
    val invalidValues = setOf("null", "none", "undefined", "govt vacancy", "latest", "released", "declared", "", "notice out")

    // Priority 1: Check vacancies FIRST! (SewayojanRepository puts last_date_text/last_date_text_hi from data.json into vacancies field)
    val cleanVacancies = job.vacancies.trim()
        .replace(REGEX_PREFIX_LAST_DATE, "")
        .replace(REGEX_PREFIX_LAST, "")
        .replace(REGEX_PREFIX_HINDI_DATE, "")
        .replace(REGEX_HTML_TAGS, "")
        .trim()

    if (!isStartDatePhrase(cleanVacancies) && cleanVacancies.isNotBlank() && cleanVacancies.lowercase() !in invalidValues && !cleanVacancies.lowercase().contains("null")) {
        val lower = cleanVacancies.lowercase()
        if (lower.contains("today") || lower.contains("आज") || lower.contains("tomorrow") || lower.contains("कल") || lower.contains("closing soon")) {
            return if (cleanVacancies.uppercase().startsWith("LAST DATE")) cleanVacancies.uppercase() else "LAST DATE: ${cleanVacancies.uppercase()}"
        }
        if (extractDateComponents(cleanVacancies) != null) {
            return if (cleanVacancies.uppercase().startsWith("LAST DATE")) cleanVacancies.uppercase() else "LAST DATE: ${cleanVacancies.uppercase()}"
        }
    }

    // Priority 2: Check statusTag
    val cleanStatusTag = job.statusTag.trim()
        .replace(REGEX_PREFIX_LAST_DATE, "")
        .replace(REGEX_PREFIX_LAST, "")
        .replace(REGEX_PREFIX_HINDI_DATE, "")
        .replace(REGEX_HTML_TAGS, "")
        .trim()

    if (!isStartDatePhrase(cleanStatusTag) && cleanStatusTag.isNotBlank() && cleanStatusTag.lowercase() !in invalidValues && !cleanStatusTag.lowercase().contains("null")) {
        val lower = cleanStatusTag.lowercase()
        if (lower.contains("today") || lower.contains("आज") || lower.contains("tomorrow") || lower.contains("कल") || lower.contains("closing soon")) {
            return if (cleanStatusTag.uppercase().startsWith("LAST DATE")) cleanStatusTag.uppercase() else "LAST DATE: ${cleanStatusTag.uppercase()}"
        }
        if (extractDateComponents(cleanStatusTag) != null) {
            return if (cleanStatusTag.uppercase().startsWith("LAST DATE")) cleanStatusTag.uppercase() else "LAST DATE: ${cleanStatusTag.uppercase()}"
        }
    }

    // Priority 3: Check description HTML date extraction
    val extractedFromDesc = HtmlDateExtractor.extractDateText(job.description)
    if (!extractedFromDesc.isNullOrBlank() && !isStartDatePhrase(extractedFromDesc)) {
        return "LAST DATE: ${extractedFromDesc.uppercase()}"
    }

    // Priority 4: Check dateText ONLY IF explicitly contains last date terms or parsed date
    val cleanDateText = job.dateText.trim()
        .replace(REGEX_PREFIX_LAST_DATE, "")
        .replace(REGEX_PREFIX_LAST, "")
        .replace(REGEX_PREFIX_HINDI_DATE, "")
        .replace(REGEX_HTML_TAGS, "")
        .trim()

    if (!isStartDatePhrase(cleanDateText) && cleanDateText.isNotBlank() && cleanDateText.lowercase() !in invalidValues && !cleanDateText.lowercase().contains("null")) {
        val lower = cleanDateText.lowercase()
        if (lower.contains("last date") || lower.contains("closing") || lower.contains("today") || lower.contains("tomorrow")) {
            return if (cleanDateText.uppercase().startsWith("LAST DATE")) cleanDateText.uppercase() else "LAST DATE: ${cleanDateText.uppercase()}"
        }
        if (extractDateComponents(cleanDateText) != null) {
            return if (cleanDateText.uppercase().startsWith("LAST DATE")) cleanDateText.uppercase() else "LAST DATE: ${cleanDateText.uppercase()}"
        }
    }

    return fallbackTag
}

private fun checkIsExpired(tag: String): Boolean {
    val lower = tag.lowercase()
    if (lower.contains("today") || lower.contains("आज") || lower.contains("tomorrow") || lower.contains("कल") || lower.contains("closing soon")) {
        return false
    }

    val components = extractDateComponents(tag)
    if (components != null) {
        val (year, month, day) = components
        val jobDateInt = year * 10000 + month * 100 + day

        val cal = java.util.Calendar.getInstance()
        val curYear = cal.get(java.util.Calendar.YEAR)
        val curMonth = cal.get(java.util.Calendar.MONTH) + 1
        val curDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val currentDateInt = curYear * 10000 + curMonth * 100 + curDay

        if (jobDateInt < currentDateInt) {
            return true // Strict past date -> EXPIRED!
        }
    }
    return false
}

private fun calculateScore(tag: String): Int {
    val lower = tag.lowercase()
    if (lower.contains("today") || lower.contains("आज")) return 0
    if (lower.contains("tomorrow") || lower.contains("कल")) return 1
    if (lower.contains("closing soon") || lower.contains("extended")) return 2

    val components = extractDateComponents(tag)
    if (components != null) {
        val (year, month, day) = components
        val cal = java.util.Calendar.getInstance()
        val curYear = cal.get(java.util.Calendar.YEAR)
        val curMonth = cal.get(java.util.Calendar.MONTH) + 1
        val curDay = cal.get(java.util.Calendar.DAY_OF_MONTH)

        val jobCal = java.util.Calendar.getInstance()
        jobCal.set(year, month - 1, day, 0, 0, 0)
        jobCal.set(java.util.Calendar.MILLISECOND, 0)

        val curCal = java.util.Calendar.getInstance()
        curCal.set(curYear, curMonth - 1, curDay, 0, 0, 0)
        curCal.set(java.util.Calendar.MILLISECOND, 0)

        val diffMs = jobCal.timeInMillis - curCal.timeInMillis
        val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()

        if (diffDays >= 0) {
            return diffDays + 3
        } else {
            return 10000
        }
    }
    return 500
}

private fun getUniqueJobKey(job: JobOpportunity): String {
    val cleanUrl = job.applyUrl.removePrefix("/post/").trim('/')
    if (cleanUrl.isNotBlank()) {
        return cleanUrl.lowercase()
    }
    val cleanTitle = job.title.lowercase()
        .replace(REGEX_KEY_CLEAN, "")
        .replace(REGEX_NON_ALPHANUM, "")
        .trim()
    return cleanTitle.ifEmpty { job.title.trim().lowercase() }
}

private fun isJobExpiredForCards(job: JobOpportunity): Boolean {
    val tag = extractLastDateTag(job, "Notice Out")
    return checkIsExpired(tag)
}

private fun calculateLastDateUrgencyScore(job: JobOpportunity): Int {
    val tag = extractLastDateTag(job, "Notice Out")
    return calculateScore(tag)
}

private fun parseMonthToNum(m: String): Int {
    val clean = m.lowercase().trim()
        .replace("st", "").replace("nd", "").replace("rd", "").replace("th", "")
    val num = clean.toIntOrNull()
    if (num != null && num in 1..12) return num

    return when {
        clean.contains("jan") || clean.contains("जन") -> 1
        clean.contains("feb") || clean.contains("फर") -> 2
        clean.contains("mar") || clean.contains("मार") -> 3
        clean.contains("apr") || clean.contains("अप्रै") || clean.contains("अप्रे") -> 4
        clean.contains("may") || clean.contains("मई") -> 5
        clean.contains("jun") || clean.contains("जू") -> 6
        clean.contains("jul") || clean.contains("जुला") -> 7
        clean.contains("aug") || clean.contains("अग") -> 8
        clean.contains("sep") || clean.contains("सित") -> 9
        clean.contains("oct") || clean.contains("अक्टू") || clean.contains("अक्टु") -> 10
        clean.contains("nov") || clean.contains("नव") || clean.contains("नम्") -> 11
        clean.contains("dec") || clean.contains("दिस") -> 12
        else -> 0
    }
}

private fun shortenJobTitleForHero(rawTitle: String): String {
    var title = rawTitle
        .replace(REGEX_TITLE_SHORTEN, "")
        .replace(REGEX_SPACES, " ")
        .trim()

    if (title.length > 32) {
        title = title.take(30).trimEnd() + "..."
    }
    return title
}

private fun buildShortSubtitle(job: JobOpportunity): String {
    val vac = if (job.vacancies.isNotBlank() && !job.vacancies.lowercase().contains("null") && !job.vacancies.lowercase().contains("2026")) {
        if (job.vacancies.lowercase().contains("vacanc") || job.vacancies.lowercase().contains("post")) job.vacancies else "${job.vacancies} Posts"
    } else "Active Vacancy"

    val extra = when {
        job.description.lowercase().contains("graduate") || job.title.lowercase().contains("cgl") -> "Graduate Degree"
        job.description.lowercase().contains("10th") || job.description.lowercase().contains("12th") -> "10th/12th Pass"
        job.description.lowercase().contains("iti") || job.description.lowercase().contains("diploma") -> "ITI/Diploma"
        job.description.lowercase().contains("bank") -> "Bank PO/Clerk"
        job.description.lowercase().contains("police") -> "Physical & Written"
        else -> "Official Notification"
    }

    return "$vac | $extra"
}
