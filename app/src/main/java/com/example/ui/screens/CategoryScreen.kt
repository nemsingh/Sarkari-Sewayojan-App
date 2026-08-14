package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JobOpportunity
import com.example.ui.theme.PrimaryRed

data class CategoryBoxData(
    val title: String,
    val icon: ImageVector,
    val items: List<JobOpportunity>,
    val isCategoryLinks: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    allJobs: List<JobOpportunity> = emptyList(),
    admitCards: List<com.example.data.local.AdmitCardItem> = emptyList(),
    examResults: List<com.example.data.local.ExamResultItem> = emptyList(),
    onCategoryClick: (String) -> Unit,
    onJobSelect: (JobOpportunity) -> Unit = {},
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var draftSearchQuery by remember { mutableStateOf("") }
    var appliedSearchQuery by remember { mutableStateOf("") }

    val allCategories = remember(allJobs, admitCards, examResults) {
        val jobsByCat = allJobs.groupBy { it.category.trim() }

        val latestJobsItems = ensureFiveCategoryJobs(
            jobsByCat["Latest Jobs"] ?: jobsByCat["Jobs"] ?: allJobs,
            allJobs,
            "Latest Jobs"
        )

        val admitCardItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Admit", ignoreCase = true) },
            allJobs,
            "Admit Card"
        )

        val resultItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Result", ignoreCase = true) },
            allJobs,
            "Result"
        )

        val answerKeyItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Answer", ignoreCase = true) },
            allJobs,
            "Answer Key"
        )

        val certItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Certificate", ignoreCase = true) || it.category.contains("Verification", ignoreCase = true) },
            allJobs,
            "Certificate Verification"
        )

        val admissionItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Admission", ignoreCase = true) || it.category.contains("Addmission", ignoreCase = true) },
            allJobs,
            "Admission"
        )

        val syllabusItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Syllabus", ignoreCase = true) },
            allJobs,
            "Syllabus"
        )

        val importantItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Important", ignoreCase = true) },
            allJobs,
            "Important"
        )

        val offlineItems = ensureFiveCategoryJobs(
            allJobs.filter { it.category.contains("Offline", ignoreCase = true) },
            allJobs,
            "Offline Job"
        )

        val categoryWiseItems = listOf(
            JobOpportunity(title = "SSC Jobs & Recruitment", category = "SSC"),
            JobOpportunity(title = "Railway RRB / RRC Vacancies", category = "Railway"),
            JobOpportunity(title = "UPSC Civil Services & NDA", category = "UPSC"),
            JobOpportunity(title = "Police Constable & SI Jobs", category = "Police"),
            JobOpportunity(title = "UPSSSC / UPPSC State Jobs", category = "UPSSSC"),
            JobOpportunity(title = "RPSC & BPSC State Recruitment", category = "State Jobs")
        )

        listOf(
            CategoryBoxData(title = "Latest Jobs", icon = Icons.Default.Work, items = latestJobsItems),
            CategoryBoxData(title = "Admit Card", icon = Icons.Default.Badge, items = admitCardItems),
            CategoryBoxData(title = "Result", icon = Icons.Default.Grade, items = resultItems),
            CategoryBoxData(title = "Answer Key", icon = Icons.Default.Description, items = answerKeyItems),
            CategoryBoxData(title = "Certificate Verification", icon = Icons.Default.Verified, items = certItems),
            CategoryBoxData(title = "Admission", icon = Icons.Default.School, items = admissionItems),
            CategoryBoxData(title = "Syllabus", icon = Icons.Default.MenuBook, items = syllabusItems),
            CategoryBoxData(title = "Important", icon = Icons.Default.FolderSpecial, items = importantItems),
            CategoryBoxData(title = "Offline Job", icon = Icons.Default.Assignment, items = offlineItems),
            CategoryBoxData(title = "Category Wise Jobs", icon = Icons.Default.Category, items = categoryWiseItems, isCategoryLinks = true)
        )
    }

    val filteredCategories = remember(allCategories, allJobs, admitCards, examResults, appliedSearchQuery) {
        if (appliedSearchQuery.isBlank()) {
            allCategories
        } else {
            buildCategoryBoxesForSearch(
                query = appliedSearchQuery,
                allJobs = allJobs,
                admitCards = admitCards,
                examResults = examResults,
                defaultCategories = allCategories
            )
        }
    }

    val categoryPairs = filteredCategories.chunked(2)
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 6.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Search bar item
        item {
            Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                BasicTextField(
                    value = draftSearchQuery,
                    onValueChange = { draftSearchQuery = it },
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
                            appliedSearchQuery = draftSearchQuery.trim()
                            keyboardController?.hide()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
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
                                if (draftSearchQuery.isEmpty()) {
                                    Text(
                                        text = "Search categories or jobs...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                innerTextField()
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (draftSearchQuery.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            draftSearchQuery = ""
                                            appliedSearchQuery = ""
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryRed,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clickable {
                                            appliedSearchQuery = draftSearchQuery.trim()
                                            keyboardController?.hide()
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
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Categories Header item
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "All Categories",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "${filteredCategories.size} Categories",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(categoryPairs) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CategoryBoxItem(
                        category = pair[0],
                        onOpenCategory = { onCategoryClick(it) },
                        onJobSelect = onJobSelect,
                        modifier = Modifier.weight(1f)
                    )
                    if (pair.size > 1) {
                        CategoryBoxItem(
                            category = pair[1],
                            onOpenCategory = { onCategoryClick(it) },
                            onJobSelect = onJobSelect,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

private fun buildCategoryBoxesForSearch(
    query: String,
    allJobs: List<JobOpportunity>,
    admitCards: List<com.example.data.local.AdmitCardItem>,
    examResults: List<com.example.data.local.ExamResultItem>,
    defaultCategories: List<CategoryBoxData>
): List<CategoryBoxData> {
    val q = query.trim().lowercase()
    if (q.isBlank()) return defaultCategories

    // Convert Admit Cards to JobOpportunity
    val admitAsJobs = admitCards.map {
        JobOpportunity(
            title = it.examTitle,
            category = "Admit Card",
            department = it.department,
            vacancies = it.examDateOrCenter,
            applyUrl = it.downloadUrl
        )
    }

    // Convert Exam Results to JobOpportunity
    val resultsAsJobs = examResults.map {
        JobOpportunity(
            title = it.examTitle,
            category = "Result",
            department = it.department,
            vacancies = it.releasedDate,
            applyUrl = it.resultUrl
        )
    }

    // Combine all jobs and ensure all items are strictly unique by title
    val combinedJobs = (allJobs + admitAsJobs + resultsAsJobs)
        .distinctBy { it.title.trim().lowercase() }

    // Filter jobs matching the search query
    val matchedJobs = combinedJobs.filter { job ->
        job.title.lowercase().contains(q) ||
        job.category.lowercase().contains(q) ||
        job.department.lowercase().contains(q) ||
        job.description.lowercase().contains(q) ||
        job.vacancies.lowercase().contains(q)
    }

    if (matchedJobs.isEmpty()) {
        return emptyList()
    }

    // Domain keywords map for each category box
    val catKeywords = mapOf(
        "Latest Jobs" to listOf("latest", "job", "recruitment", "vacancy", "post", "apply"),
        "Admit Card" to listOf("admit", "hall ticket", "call letter", "exam date", "card"),
        "Result" to listOf("result", "marks", "merit", "score", "declared"),
        "Answer Key" to listOf("answer", "key", "objection", "solution"),
        "Certificate Verification" to listOf("certificate", "verification", "document", "dv"),
        "Admission" to listOf("admission", "entrance", "counseling", "form"),
        "Syllabus" to listOf("syllabus", "pattern", "exam pattern", "curriculum"),
        "Important" to listOf("important", "notice", "date", "correction", "alert"),
        "Offline Job" to listOf("offline", "postal", "offline form")
    )

    val updatedBoxes = mutableListOf<CategoryBoxData>()

    for (catBox in defaultCategories) {
        if (catBox.isCategoryLinks) {
            val filteredLinks = catBox.items.filter { link ->
                link.title.lowercase().contains(q) || link.category.lowercase().contains(q)
            }
            val linkItems = if (filteredLinks.isNotEmpty()) {
                filteredLinks
            } else {
                matchedJobs.map {
                    JobOpportunity(title = "${it.title} (${it.category})", category = it.category)
                }
            }
            val uniqueLinkItems = linkItems.distinctBy { it.title.trim().lowercase() }.take(5)
            if (uniqueLinkItems.isNotEmpty()) {
                updatedBoxes.add(catBox.copy(items = uniqueLinkItems))
            }
            continue
        }

        val keywords = catKeywords[catBox.title] ?: emptyList()

        // 1. Primary category matches from query search results
        val categoryPrimaryMatches = matchedJobs.filter { job ->
            job.category.contains(catBox.title, ignoreCase = true) ||
            keywords.any { kw -> job.category.contains(kw, ignoreCase = true) || job.title.contains(kw, ignoreCase = true) }
        }

        val boxSelectedItems = mutableListOf<JobOpportunity>()
        val boxUsedTitles = mutableSetOf<String>()

        // Add category primary matches first
        for (job in categoryPrimaryMatches) {
            val normTitle = job.title.trim().lowercase()
            if (boxUsedTitles.add(normTitle)) {
                boxSelectedItems.add(job)
                if (boxSelectedItems.size >= 5) break
            }
        }

        // 2. If less than 5, supplement with other matched jobs from query results
        if (boxSelectedItems.size < 5) {
            for (job in matchedJobs) {
                val normTitle = job.title.trim().lowercase()
                if (boxUsedTitles.add(normTitle)) {
                    boxSelectedItems.add(job)
                    if (boxSelectedItems.size >= 5) break
                }
            }
        }

        if (boxSelectedItems.isNotEmpty()) {
            updatedBoxes.add(catBox.copy(items = boxSelectedItems.take(5)))
        }
    }

    return updatedBoxes
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
private fun CategoryBoxItem(
    category: CategoryBoxData,
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
            // Red Header with White Icon and White Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryRed)
                    .clickable { onOpenCategory(category.title) }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // List of 5 Titles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp, horizontal = 3.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                category.items.take(5).forEachIndexed { index, jobItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (category.isCategoryLinks) {
                                    onOpenCategory(jobItem.title)
                                } else {
                                    onJobSelect(jobItem)
                                }
                            }
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

                    if (index < category.items.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFFF2F2F2),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                }
            }

            // View More Button at Bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCategory(category.title) }
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
