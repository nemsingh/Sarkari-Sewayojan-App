package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JobOpportunity
import com.example.ui.theme.PrimaryRed

data class GenericCategoryItem(
    val title: String,
    val lastDate: String = "",
    val associatedJob: JobOpportunity? = null
)

@Composable
fun CategoryDetailScreen(
    categoryTitle: String,
    allJobs: List<JobOpportunity>,
    onBack: () -> Unit,
    onJobSelect: (JobOpportunity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLatestJobsCategory = categoryTitle == "Latest Jobs" || categoryTitle == "Jobs"

    // Retrieve specific items for this category
    val categoryItems = remember(categoryTitle, allJobs) {
        getCategorySpecificItems(categoryTitle, allJobs)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (categoryItems.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = PrimaryRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (categoryTitle.contains("Save", ignoreCase = true)) "No Saved Jobs Found" else "No Items Available",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (categoryTitle.contains("Save", ignoreCase = true)) "When you click 'Save' or 'Bookmark' on any job post, it will be saved here on your device for offline reading." else "Check back soon for new updates.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = categoryItems,
                    key = { index, itemData ->
                        val k = itemData.associatedJob?.applyUrl?.ifBlank { itemData.title } ?: itemData.title
                        "${index}_$k"
                    }
                ) { _, itemData ->
                    if (isLatestJobsCategory) {
                        // Latest Job Category Card: Title + Last Date + Apply Online button only
                        LatestJobCategoryCard(
                            item = itemData,
                            onItemClick = {
                                val jobToSelect = itemData.associatedJob ?: JobOpportunity(title = itemData.title, category = categoryTitle)
                                onJobSelect(jobToSelect)
                            },
                            onApplyClick = {
                                val jobToSelect = itemData.associatedJob ?: JobOpportunity(title = itemData.title, category = categoryTitle)
                                onJobSelect(jobToSelect)
                            }
                        )
                    } else {
                        // Other Categories Card: Title + Visit Now button only (No Last Date)
                        OtherCategoryCard(
                            item = itemData,
                            onItemClick = {
                                val jobToSelect = itemData.associatedJob ?: JobOpportunity(title = itemData.title, category = categoryTitle)
                                onJobSelect(jobToSelect)
                            },
                            onVisitClick = {
                                val jobToSelect = itemData.associatedJob ?: JobOpportunity(title = itemData.title, category = categoryTitle)
                                onJobSelect(jobToSelect)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LatestJobCategoryCard(
    item: GenericCategoryItem,
    onItemClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Row with Last Date and Apply Online button only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Last Date only
                val displayDate = formatDisplayLastDate(item.lastDate, item.associatedJob?.dateText)
                Text(
                    text = "Last Date: $displayDate",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = PrimaryRed
                )

                // Apply Online button
                Button(
                    onClick = onApplyClick,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                ) {
                    Text(
                        text = "Apply Online",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OtherCategoryCard(
    item: GenericCategoryItem,
    onItemClick: () -> Unit,
    onVisitClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title only
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Visit Now button only
            Button(
                onClick = onVisitClick,
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text(
                    text = "Visit Now",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

private fun getCategorySpecificItems(
    categoryTitle: String,
    allJobs: List<JobOpportunity>
): List<GenericCategoryItem> {
    if (allJobs.isEmpty()) {
        return emptyList()
    }

    val lowerCatTitle = categoryTitle.lowercase().trim()

    if (lowerCatTitle.contains("save") || lowerCatTitle.contains("bookmark")) {
        return allJobs.filter { it.isSaved }.map { job ->
            GenericCategoryItem(
                title = job.title,
                lastDate = formatDisplayLastDate(job.vacancies, job.dateText),
                associatedJob = job
            )
        }
    }

    val matchedJobs = when {
        lowerCatTitle in listOf("latest jobs", "jobs") -> {
            allJobs.filter { it.category.contains("Jobs", ignoreCase = true) || it.category.contains("Latest", ignoreCase = true) }
                .ifEmpty { allJobs }
        }

        lowerCatTitle.contains("admit") -> {
            allJobs.filter { it.category.contains("Admit", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Admit", ignoreCase = true) } }
        }

        lowerCatTitle.contains("result") -> {
            allJobs.filter { it.category.contains("Result", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Result", ignoreCase = true) } }
        }

        lowerCatTitle.contains("answer") -> {
            allJobs.filter { it.category.contains("Answer", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Answer", ignoreCase = true) || it.title.contains("Key", ignoreCase = true) } }
        }

        lowerCatTitle.contains("certificate") || lowerCatTitle.contains("verification") -> {
            allJobs.filter { it.category.contains("Certificate", ignoreCase = true) || it.category.contains("Verification", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Certificate", ignoreCase = true) || it.title.contains("Verification", ignoreCase = true) } }
        }

        lowerCatTitle.contains("admission") || lowerCatTitle.contains("addmission") -> {
            allJobs.filter { it.category.contains("Admission", ignoreCase = true) || it.category.contains("Addmission", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Admission", ignoreCase = true) || it.title.contains("Addmission", ignoreCase = true) } }
        }

        lowerCatTitle.contains("syllabus") -> {
            allJobs.filter { it.category.contains("Syllabus", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Syllabus", ignoreCase = true) } }
        }

        lowerCatTitle.contains("important") -> {
            allJobs.filter { it.category.contains("Important", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Important", ignoreCase = true) } }
        }

        lowerCatTitle.contains("offline") -> {
            allJobs.filter { it.category.contains("Offline", ignoreCase = true) }
                .ifEmpty { allJobs.filter { it.title.contains("Offline", ignoreCase = true) } }
        }

        else -> {
            filterJobsBySmartKeywords(categoryTitle, allJobs)
        }
    }

    val finalJobs = matchedJobs.ifEmpty { getFallbackCategoryJobs(categoryTitle) }

    return finalJobs.map { job ->
        GenericCategoryItem(
            title = job.title,
            lastDate = formatDisplayLastDate(job.vacancies, job.dateText),
            associatedJob = job
        )
    }
}

private fun filterJobsBySmartKeywords(
    categoryTitle: String,
    allJobs: List<JobOpportunity>
): List<JobOpportunity> {
    val titleLower = categoryTitle.lowercase().trim()
    val keywords = mutableSetOf<String>()

    when {
        titleLower.contains("ssc") -> {
            keywords.addAll(listOf("ssc", "cgl", "chsl", "mts", "cpo", "gd", "steno"))
        }
        titleLower.contains("railway") || titleLower.contains("rrb") || titleLower.contains("rrc") -> {
            keywords.addAll(listOf("railway", "rrb", "rrc", "ntpc", "alp", "group d", "technician", "loco"))
        }
        titleLower.contains("upsc") || titleLower.contains("nda") || titleLower.contains("civil") -> {
            keywords.addAll(listOf("upsc", "nda", "cds", "ias", "ips", "ifs", "civil service"))
        }
        titleLower.contains("police") || titleLower.contains("constable") || titleLower.contains("si ") || titleLower.contains("sub inspector") -> {
            keywords.addAll(listOf("police", "constable", "sub inspector", "sub-inspector", "si", "homeguard"))
        }
        titleLower.contains("upsssc") || titleLower.contains("uppsc") -> {
            keywords.addAll(listOf("upsssc", "uppsc", "lekhpal", "pet", "vdo", "uttar pradesh"))
        }
        titleLower.contains("rpsc") || titleLower.contains("bpsc") -> {
            keywords.addAll(listOf("rpsc", "bpsc", "rajasthan", "bihar"))
        }
        titleLower.contains("bank") || titleLower.contains("banking") -> {
            keywords.addAll(listOf("bank", "banking", "ibps", "sbi", "rbi", "po", "clerk", "nabard"))
        }
        titleLower.contains("defense") || titleLower.contains("defence") || titleLower.contains("army") || titleLower.contains("navy") || titleLower.contains("airforce") -> {
            keywords.addAll(listOf("defense", "defence", "army", "navy", "airforce", "agniveer", "afcat"))
        }
        titleLower.contains("teach") || titleLower.contains("tet") || titleLower.contains("ctet") -> {
            keywords.addAll(listOf("teach", "teacher", "tet", "ctet", "tgt", "pgt", "dsssb"))
        }
        titleLower.contains("court") -> {
            keywords.addAll(listOf("court", "high court", "judiciary", "judge", "steno"))
        }
        titleLower.contains("medical") || titleLower.contains("nurse") || titleLower.contains("aiims") -> {
            keywords.addAll(listOf("medical", "nurse", "nursing", "aiims", "neet", "doctor", "health"))
        }
    }

    val noiseWords = setOf(
        "jobs", "job", "recruitment", "vacancies", "vacancy", "post", "posts",
        "state", "all", "online", "form", "2026", "2025", "and", "or", "for",
        "in", "the", "with", "wise", "category"
    )
    val tokens = titleLower.replace(Regex("[^a-z0-9\\s]"), " ")
        .split(Regex("\\s+"))
        .map { it.trim() }
        .filter { it.length >= 2 && it !in noiseWords }

    keywords.addAll(tokens)

    if (keywords.isEmpty()) {
        keywords.add(titleLower)
    }

    val matchedJobs = allJobs.filter { job ->
        val searchCorpus = "${job.title} ${job.category} ${job.department} ${job.description}".lowercase()
        keywords.any { kw -> searchCorpus.contains(kw) }
    }

    return matchedJobs.sortedByDescending { job ->
        val titleAndCat = "${job.title} ${job.category}".lowercase()
        keywords.count { kw -> titleAndCat.contains(kw) }
    }
}

private fun getFallbackCategoryJobs(categoryTitle: String): List<JobOpportunity> {
    val titleLower = categoryTitle.lowercase().trim()
    return when {
        titleLower.contains("ssc") -> listOf(
            JobOpportunity(title = "SSC CGL Combined Graduate Level Recruitment 2026", category = categoryTitle, department = "Staff Selection Commission"),
            JobOpportunity(title = "SSC CHSL Higher Secondary 10+2 Online Form", category = categoryTitle, department = "Staff Selection Commission"),
            JobOpportunity(title = "SSC GD Constable Recruitment in CAPF & Assam Rifles", category = categoryTitle, department = "Staff Selection Commission"),
            JobOpportunity(title = "SSC Multi Tasking Staff (MTS) & Havaldar Bharti", category = categoryTitle, department = "Staff Selection Commission"),
            JobOpportunity(title = "SSC Sub-Inspector (SI) in Delhi Police & Central Armed Police", category = categoryTitle, department = "Staff Selection Commission")
        )
        titleLower.contains("railway") || titleLower.contains("rrb") || titleLower.contains("rrc") -> listOf(
            JobOpportunity(title = "Railway RRB NTPC Graduate & Non-Technical Posts", category = categoryTitle, department = "Indian Railways"),
            JobOpportunity(title = "Railway RRB Assistant Loco Pilot (ALP) Vacancy", category = categoryTitle, department = "Indian Railways"),
            JobOpportunity(title = "Railway RRC Group D Recruitment 2026", category = categoryTitle, department = "Indian Railways"),
            JobOpportunity(title = "Railway RRB Technician Grade I & Grade III Posts", category = categoryTitle, department = "Indian Railways"),
            JobOpportunity(title = "Railway RRC Apprentice Various Trade Recruitment", category = categoryTitle, department = "Indian Railways")
        )
        titleLower.contains("upsc") || titleLower.contains("nda") || titleLower.contains("civil") -> listOf(
            JobOpportunity(title = "UPSC IAS / IFS Civil Services Examination 2026", category = categoryTitle, department = "UPSC"),
            JobOpportunity(title = "UPSC NDA & NA I Examination 2026", category = categoryTitle, department = "UPSC"),
            JobOpportunity(title = "UPSC Combined Defence Services (CDS I) Online Form", category = categoryTitle, department = "UPSC"),
            JobOpportunity(title = "UPSC Combined Medical Services (CMS) Recruitment", category = categoryTitle, department = "UPSC"),
            JobOpportunity(title = "UPSC Central Armed Police Forces (CAPF AC) Vacancies", category = categoryTitle, department = "UPSC")
        )
        titleLower.contains("police") || titleLower.contains("constable") || titleLower.contains("si") -> listOf(
            JobOpportunity(title = "UP Police Constable Direct Recruitment 2026", category = categoryTitle, department = "Police Recruitment Board"),
            JobOpportunity(title = "Police Sub-Inspector (SI) & Platoon Commander Posts", category = categoryTitle, department = "Police Recruitment Board"),
            JobOpportunity(title = "Police Radio Operator & Computer Operator Vacancy", category = categoryTitle, department = "Police Recruitment Board"),
            JobOpportunity(title = "Police Jail Warder & Fireman Bharti", category = categoryTitle, department = "Police Recruitment Board"),
            JobOpportunity(title = "State Police Home Guard & Driver Recruitment", category = categoryTitle, department = "Police Recruitment Board")
        )
        titleLower.contains("upsssc") || titleLower.contains("uppsc") -> listOf(
            JobOpportunity(title = "UPSSSC Preliminary Eligibility Test (PET) 2026", category = categoryTitle, department = "UPSSSC"),
            JobOpportunity(title = "UPSSSC Rajasva Lekhpal & Revenue Inspector Posts", category = categoryTitle, department = "UPSSSC"),
            JobOpportunity(title = "UPPSC Combined State Upper Subordinate Exam (PCS)", category = categoryTitle, department = "UPPSC"),
            JobOpportunity(title = "UPSSSC Gram Vikas Adhikari (VDO) Recruitment", category = categoryTitle, department = "UPSSSC"),
            JobOpportunity(title = "UPPSC Review Officer (RO) / Assistant Review Officer (ARO)", category = categoryTitle, department = "UPPSC")
        )
        titleLower.contains("rpsc") || titleLower.contains("bpsc") || titleLower.contains("state") -> listOf(
            JobOpportunity(title = "BPSC Combined Competitive Examination 2026", category = categoryTitle, department = "BPSC"),
            JobOpportunity(title = "RPSC RAS / RTS State Service Examination", category = categoryTitle, department = "RPSC"),
            JobOpportunity(title = "BPSC Teacher Recruitment Examination (TRE 4.0)", category = categoryTitle, department = "BPSC"),
            JobOpportunity(title = "RPSC Senior Teacher & 2nd Grade Bharti", category = categoryTitle, department = "RPSC"),
            JobOpportunity(title = "State Public Service Commission Assistant Engineer Posts", category = categoryTitle, department = "State PSC")
        )
        titleLower.contains("bank") || titleLower.contains("banking") -> listOf(
            JobOpportunity(title = "IBPS PO / Management Trainee Recruitment 2026", category = categoryTitle, department = "IBPS"),
            JobOpportunity(title = "SBI Probationary Officer (PO) Online Application Form", category = categoryTitle, department = "State Bank of India"),
            JobOpportunity(title = "IBPS Clerk XIV Recruitment Vacancies", category = categoryTitle, department = "IBPS"),
            JobOpportunity(title = "RBI Assistant Direct Recruitment 2026", category = categoryTitle, department = "Reserve Bank of India"),
            JobOpportunity(title = "IBPS RRB Officer Scale I, II, III & Office Assistant", category = categoryTitle, department = "IBPS")
        )
        titleLower.contains("defense") || titleLower.contains("defence") || titleLower.contains("army") || titleLower.contains("navy") || titleLower.contains("airforce") -> listOf(
            JobOpportunity(title = "Indian Army Agnipath Agniveer General Duty & Technical", category = categoryTitle, department = "Indian Army"),
            JobOpportunity(title = "Indian Navy SSR & MR Agniveer Entry", category = categoryTitle, department = "Indian Navy"),
            JobOpportunity(title = "Indian Air Force Agniveervayu Recruitment", category = categoryTitle, department = "Indian Air Force"),
            JobOpportunity(title = "Indian Coast Guard Navik DB / GD & Yantrik", category = categoryTitle, department = "Coast Guard"),
            JobOpportunity(title = "AFCAT Air Force Common Admission Test 2026", category = categoryTitle, department = "Indian Air Force")
        )
        titleLower.contains("teach") || titleLower.contains("tet") || titleLower.contains("ctet") -> listOf(
            JobOpportunity(title = "CTET Central Teacher Eligibility Test 2026", category = categoryTitle, department = "CBSE"),
            JobOpportunity(title = "DSSSB TGT, PGT & Assistant Teacher Recruitment", category = categoryTitle, department = "DSSSB"),
            JobOpportunity(title = "UP TGT PGT Secondary Education Service Selection Board", category = categoryTitle, department = "UPSESSB"),
            JobOpportunity(title = "EMRS Eklavya Model School Teaching & Non-Teaching Posts", category = categoryTitle, department = "EMRS"),
            JobOpportunity(title = "KVS Kendriya Vidyalaya Primary Teacher (PRT) Vacancies", category = categoryTitle, department = "KVS")
        )
        else -> listOf(
            JobOpportunity(title = "$categoryTitle - Latest Official Recruitment Notification 2026", category = categoryTitle, department = "Govt Organization"),
            JobOpportunity(title = "$categoryTitle - Apply Online Form & Eligibility Details", category = categoryTitle, department = "Govt Organization"),
            JobOpportunity(title = "$categoryTitle - Vacancy Syllabus & Exam Pattern Release", category = categoryTitle, department = "Govt Organization"),
            JobOpportunity(title = "$categoryTitle - Admit Card & Exam Date Notice", category = categoryTitle, department = "Govt Organization"),
            JobOpportunity(title = "$categoryTitle - Final Merit List & Cut Off Marks", category = categoryTitle, department = "Govt Organization")
        )
    }
}

fun formatDisplayLastDate(rawLastDate: String, rawDateText: String? = null): String {
    val invalidValues = setOf("null", "none", "undefined", "govt vacancy", "latest", "released", "declared", "", "notice out")
    var dateStr = rawLastDate.trim()

    dateStr = dateStr
        .replace(Regex("<[^>]*>"), "")
        .replace(Regex("(?i)^(?:Apply\\s+Online\\s+|Registration\\s+|Online\\s+Form\\s+)?Last\\s+Date\\s*:?\\s*"), "")
        .replace(Regex("(?i)^Last\\s*:\\s*"), "")
        .replace(Regex("(?i)^Extended\\s*:?\\s*|Extended\\s+Date\\s*:?\\s*|Extended\\s+"), "")
        .trim()

    if (dateStr.lowercase() in invalidValues || dateStr.lowercase().contains("null")) {
        dateStr = rawDateText?.trim() ?: ""
        dateStr = dateStr
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("(?i)^(?:Apply\\s+Online\\s+|Registration\\s+|Online\\s+Form\\s+)?Last\\s+Date\\s*:?\\s*"), "")
            .replace(Regex("(?i)^Last\\s*:\\s*"), "")
            .trim()
    }

    if (dateStr.lowercase() in invalidValues || dateStr.lowercase().contains("null") || dateStr.isBlank()) {
        dateStr = "Notice Out"
    }

    return dateStr
}
