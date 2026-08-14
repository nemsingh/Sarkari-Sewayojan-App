package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AdmitCardItem
import com.example.data.local.ExamResultItem
import com.example.data.local.JobOpportunity
import com.example.data.local.SewayojanDao
import com.example.data.local.SewayojanDatabase
import com.example.data.local.UserApplication
import com.example.util.HtmlDateExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SewayojanRepository(private val dao: SewayojanDao) {

    private val syncMutex = Mutex()
    private var lastSyncedJsonHash: Int = 0
    private var lastEtag: String? = null
    private var lastModifiedHeader: String? = null

    val allJobs: Flow<List<JobOpportunity>> = dao.getAllJobs()
    val allAdmitCards: Flow<List<AdmitCardItem>> = dao.getAllAdmitCards()
    val allExamResults: Flow<List<ExamResultItem>> = dao.getAllExamResults()
    val userApplications: Flow<List<UserApplication>> = dao.getUserApplications()

    /**
     * Live sync from Sarkari Sewayojan published JSON backend (data.json).
     * Strictly uses Vercel-hosted JSON with ZERO Firestore reads.
     */
    suspend fun syncWithFirebase(forceFetchJson: Boolean = false) = withContext(Dispatchers.IO) {
        if (forceFetchJson) {
            lastSyncedJsonHash = 0
            lastEtag = null
            lastModifiedHeader = null
        }
        if (!syncMutex.tryLock()) {
            Log.d("SmartSync", "Sync already in progress. Skipping duplicate request.")
            return@withContext
        }
        try {
            val existingJobsCount = try { dao.getAllJobs().first().size } catch (e: Exception) { 0 }

            val savedJobTitles = try {
                dao.getAllJobs().first().filter { it.isSaved }.map { it.title.trim().lowercase() }.toSet()
            } catch (e: Exception) {
                emptySet()
            }
            val savedAdmitTitles = try {
                dao.getAllAdmitCards().first().filter { it.isSaved }.map { it.examTitle.trim().lowercase() }.toSet()
            } catch (e: Exception) {
                emptySet()
            }
            val savedResultTitles = try {
                dao.getAllExamResults().first().filter { it.isSaved }.map { it.examTitle.trim().lowercase() }.toSet()
            } catch (e: Exception) {
                emptySet()
            }

            // Fetch published dataset from Vercel JSON with Validation
            var conn: HttpURLConnection? = null
            try {
                var currentUrl = "https://sarkarisewayojan.com/data.json"
                var responseCode = -1
                var redirectCount = 0

                while (redirectCount < 5) {
                    val url = URL(currentUrl)
                    conn = url.openConnection() as HttpURLConnection
                    conn.useCaches = true
                    conn.requestMethod = "GET"
                    conn.connectTimeout = 12000
                    conn.readTimeout = 18000
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android App)")

                    if (!forceFetchJson && existingJobsCount > 0) {
                        if (!lastEtag.isNullOrBlank()) {
                            conn.setRequestProperty("If-None-Match", lastEtag)
                        }
                        if (!lastModifiedHeader.isNullOrBlank()) {
                            conn.setRequestProperty("If-Modified-Since", lastModifiedHeader)
                        }
                    } else if (forceFetchJson) {
                        conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
                        conn.setRequestProperty("Pragma", "no-cache")
                    }

                    responseCode = conn.responseCode
                    if (responseCode == 301 || responseCode == 302 || responseCode == 307 || responseCode == 308) {
                        val redirectUrl = conn.getHeaderField("Location")
                        if (!redirectUrl.isNullOrBlank()) {
                            currentUrl = redirectUrl
                            redirectCount++
                            conn.disconnect()
                            continue
                        }
                    }
                    break
                }

                if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) { // 304 Not Modified
                    Log.d("SmartSync", "HTTP 304 Not Modified: Vercel JSON unchanged. 0 bytes body downloaded.")
                    return@withContext
                }

                if (responseCode == 200 && conn != null) {
                    val newEtag = conn.getHeaderField("ETag")
                    val newLastMod = conn.getHeaderField("Last-Modified")
                    if (!newEtag.isNullOrBlank()) lastEtag = newEtag
                    if (!newLastMod.isNullOrBlank()) lastModifiedHeader = newLastMod

                    val jsonString = conn.inputStream.bufferedReader().use { it.readText() }

                    // Safety Validation: Must be non-empty and valid JSON
                    if (jsonString.isBlank() || !jsonString.trim().startsWith("{")) {
                        Log.e("SmartSync", "Invalid/Empty JSON received from Vercel. Preserving local Room data.")
                        return@withContext
                    }

                    val currentHash = jsonString.hashCode()
                    if (!forceFetchJson && currentHash == lastSyncedJsonHash && existingJobsCount > 50) {
                        Log.d("SmartSync", "JSON dataset hash matches local. Skipping Room DB re-write.")
                        return@withContext
                    }

                    val root = try {
                        JSONObject(jsonString)
                    } catch (ex: Exception) {
                        Log.e("SmartSync", "Corrupt JSON format from Vercel: ${ex.message}. Preserving local Room data.")
                        return@withContext
                    }

                    lastSyncedJsonHash = currentHash

                    val categoriesMap = mutableMapOf<String, String>()
                    if (root.has("categories")) {
                        val catsArr = root.getJSONArray("categories")
                        for (i in 0 until catsArr.length()) {
                            val obj = catsArr.getJSONObject(i)
                            val id = obj.optString("id")
                            val name = obj.optString("name")
                            if (id.isNotBlank() && name.isNotBlank()) {
                                categoriesMap[id] = name
                            }
                        }
                    }

                    val postsMap = mutableMapOf<String, JSONObject>()
                    if (root.has("posts")) {
                        val postsArr = root.getJSONArray("posts")
                        for (i in 0 until postsArr.length()) {
                            val pObj = postsArr.getJSONObject(i)
                            val slug = pObj.optString("slug", pObj.optString("id")).trim('/')
                            val id = pObj.optString("id").trim('/')
                            val nameOfPost = pObj.optString("name_of_post", pObj.optString("title")).trim()

                            if (slug.isNotBlank()) {
                                postsMap[slug] = pObj
                                postsMap[slug.lowercase()] = pObj
                                val leafSlug = slug.substringAfterLast('/')
                                postsMap[leafSlug] = pObj
                                postsMap[leafSlug.lowercase()] = pObj
                            }
                            if (id.isNotBlank()) {
                                postsMap[id] = pObj
                                postsMap[id.lowercase()] = pObj
                            }
                            if (nameOfPost.isNotBlank()) {
                                postsMap[nameOfPost.lowercase()] = pObj
                            }
                        }
                    }

                    val jobsList = mutableListOf<JobOpportunity>()
                    val admitCardsList = mutableListOf<AdmitCardItem>()
                    val examResultsList = mutableListOf<ExamResultItem>()

                    if (root.has("category_links")) {
                        val linksArr = root.getJSONArray("category_links")
                        for (i in 0 until linksArr.length()) {
                            val linkObj = linksArr.getJSONObject(i)
                            val catId = linkObj.optString("category_id")
                            val catName = categoriesMap[catId] ?: "Latest Jobs"
                            val title = linkObj.optString("title")
                            if (title.isBlank()) continue

                            val urlStr = linkObj.optString("url")
                            val isNew = linkObj.optBoolean("is_new", false)
                            val postDate = if (linkObj.has("post_date") && !linkObj.isNull("post_date")) linkObj.optString("post_date") else null

                            val linkLastDate = if (linkObj.has("last_date_text") && !linkObj.isNull("last_date_text")) {
                                linkObj.optString("last_date_text")
                            } else if (linkObj.has("last_date_text_hi") && !linkObj.isNull("last_date_text_hi")) {
                                linkObj.optString("last_date_text_hi")
                            } else null

                            val cleanSlug = urlStr
                                .replace("https://sarkarisewayojan.com", "")
                                .removePrefix("/post/")
                                .trim('/')
                                .substringAfterLast('/')

                            val matchedPost = postsMap[cleanSlug] ?: postsMap[cleanSlug.lowercase()] ?: postsMap[title.trim().lowercase()]
                            val postLastDate = matchedPost?.optString("last_date_text", "")?.ifBlank { matchedPost?.optString("last_date_text_hi", "") }
                            val effectiveLastDate = if (!linkLastDate.isNullOrBlank()) linkLastDate else if (!postLastDate.isNullOrBlank()) postLastDate else null

                            val shortInfo = matchedPost?.optString("short_info", "") ?: ""
                            val searchCorpus = matchedPost?.optString("search_corpus", "") ?: ""
                            val detailsHtml = matchedPost?.optString("details_html", "") ?: matchedPost?.optString("content", "") ?: ""
                            val fullCorpus = "$shortInfo $searchCorpus $detailsHtml"

                            val extractedLastDate = extractCleanLastDate(
                                lastDateText = effectiveLastDate,
                                postDate = postDate,
                                shortInfo = shortInfo,
                                title = title,
                                searchCorpus = fullCorpus
                            )
                            val extractedPostDate = extractCleanPostDate(postDate)

                            val lowerCat = catName.lowercase()
                            val isJobSaved = savedJobTitles.contains(title.trim().lowercase())
                            val jobItem = JobOpportunity(
                                title = title,
                                category = catName,
                                department = "Sarkari Sewayojan",
                                vacancies = extractedLastDate,
                                dateText = extractedPostDate,
                                statusTag = if (isNew) "NEW" else "ACTIVE",
                                description = shortInfo,
                                applyUrl = urlStr,
                                isSaved = isJobSaved
                            )
                            jobsList.add(jobItem)

                            if (lowerCat.contains("admit")) {
                                val isAdmitSaved = savedAdmitTitles.contains(title.trim().lowercase())
                                admitCardsList.add(
                                    AdmitCardItem(
                                        examTitle = title,
                                        department = "Sarkari Sewayojan",
                                        tag = catName,
                                        statusBadge = if (isNew) "New" else "Active",
                                        examDateOrCenter = extractedLastDate,
                                        downloadUrl = urlStr,
                                        isSaved = isAdmitSaved
                                    )
                                )
                            } else if (lowerCat.contains("result") || lowerCat.contains("answer")) {
                                val isResultSaved = savedResultTitles.contains(title.trim().lowercase())
                                examResultsList.add(
                                    ExamResultItem(
                                        examTitle = title,
                                        department = "Sarkari Sewayojan",
                                        tag = catName,
                                        releasedDate = extractedPostDate,
                                        isNew = isNew,
                                        resultUrl = urlStr,
                                        isSaved = isResultSaved
                                    )
                                )
                            }
                        }
                    }

                    // Add all direct posts as jobs
                    if (root.has("posts")) {
                        val postsArr = root.getJSONArray("posts")
                        for (i in 0 until postsArr.length()) {
                            val pObj = postsArr.getJSONObject(i)
                            val nameOfPost = pObj.optString("name_of_post", pObj.optString("title"))
                            val slug = pObj.optString("slug", pObj.optString("id"))
                            val postDate = if (pObj.has("post_date") && !pObj.isNull("post_date")) pObj.optString("post_date") else null
                            val lastDateText = if (pObj.has("last_date_text") && !pObj.isNull("last_date_text")) pObj.optString("last_date_text") else if (pObj.has("last_date_text_hi") && !pObj.isNull("last_date_text_hi")) pObj.optString("last_date_text_hi") else null
                            val shortInfo = pObj.optString("short_info", "")
                            val searchCorpus = pObj.optString("search_corpus", "")
                            val detailsHtml = pObj.optString("details_html", "") ?: pObj.optString("content", "") ?: ""
                            val fullCorpus = "$shortInfo $searchCorpus $detailsHtml"

                            if (nameOfPost.isNotBlank()) {
                                val extractedLastDate = extractCleanLastDate(
                                    lastDateText = lastDateText,
                                    postDate = postDate,
                                    shortInfo = shortInfo,
                                    title = nameOfPost,
                                    searchCorpus = fullCorpus
                                )
                                val extractedPostDate = extractCleanPostDate(postDate)
                                val isDirectJobSaved = savedJobTitles.contains(nameOfPost.trim().lowercase())

                                jobsList.add(
                                    JobOpportunity(
                                        title = nameOfPost,
                                        category = "Latest Jobs",
                                        department = "Sarkari Sewayojan",
                                        vacancies = extractedLastDate,
                                        dateText = extractedPostDate,
                                        statusTag = "NEW",
                                        description = shortInfo,
                                        applyUrl = "/post/$slug",
                                        isSaved = isDirectJobSaved
                                    )
                                )
                            }
                        }
                    }

                    val distinctJobs = jobsList.distinctBy { it.title.trim().lowercase() }
                    if (distinctJobs.isNotEmpty()) {
                        dao.replaceJobsInTx(distinctJobs)
                    }

                    val distinctAdmit = admitCardsList.distinctBy { it.examTitle.trim().lowercase() }
                    if (distinctAdmit.isNotEmpty()) {
                        dao.replaceAdmitCardsInTx(distinctAdmit)
                    }

                    val distinctResults = examResultsList.distinctBy { it.examTitle.trim().lowercase() }
                    if (distinctResults.isNotEmpty()) {
                        dao.replaceExamResultsInTx(distinctResults)
                    }

                    Log.d("SmartSync", "Vercel JSON sync complete! Updated Room DB with ${distinctJobs.size} jobs, ${distinctAdmit.size} admit cards, ${distinctResults.size} results. Firestore Reads = ZERO.")
                }
            } catch (e: Exception) {
                Log.e("SmartSync", "Error fetching data.json: ${e.message}. Local Room DB preserved.")
            } finally {
                try { conn?.disconnect() } catch (_: Exception) {}
            }
        } finally {
            syncMutex.unlock()
        }
    }

    fun startRealtimeSync(coroutineScope: kotlinx.coroutines.CoroutineScope) {
        Log.d("SmartSync", "FCM Mode Enabled: Zero Firestore reads. Listening for FCM push signals from Admin panel.")
    }

    suspend fun initializeDefaultDataIfEmpty() {
        // Left empty - real data loaded from Room DB / Vercel JSON.
    }

    companion object {
        @Volatile
        private var INSTANCE: SewayojanRepository? = null

        fun getInstance(context: Context): SewayojanRepository {
            return INSTANCE ?: synchronized(this) {
                val db = SewayojanDatabase.getDatabase(context)
                val instance = SewayojanRepository(db.sewayojanDao())
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun toggleSaveJob(job: JobOpportunity) {
        val allCurrent = dao.getAllJobs().first()
        val cleanTitle = job.title.trim().lowercase()
        val existing = allCurrent.find {
            (it.id != 0 && it.id == job.id) ||
            it.title.trim().lowercase() == cleanTitle
        }
        if (existing != null) {
            val updated = existing.copy(isSaved = !existing.isSaved)
            dao.updateJob(updated)
        } else {
            val newSavedState = !job.isSaved
            val inserted = job.copy(id = 0, isSaved = newSavedState)
            dao.insertJobs(listOf(inserted))
        }
    }

    suspend fun toggleSaveAdmitCard(card: AdmitCardItem) {
        val allCurrent = dao.getAllAdmitCards().first()
        val cleanTitle = card.examTitle.trim().lowercase()
        val existing = allCurrent.find {
            (it.id != 0 && it.id == card.id) ||
            it.examTitle.trim().lowercase() == cleanTitle
        }
        if (existing != null) {
            val updated = existing.copy(isSaved = !existing.isSaved)
            dao.updateAdmitCard(updated)
        } else {
            val newSavedState = !card.isSaved
            val inserted = card.copy(id = 0, isSaved = newSavedState)
            dao.insertAdmitCards(listOf(inserted))
        }
    }

    suspend fun submitApplication(title: String) {
        val randomReg = "SWJ2026-" + (100000..999999).random()
        dao.insertApplication(
            UserApplication(
                title = title,
                registrationNo = randomReg,
                appliedDate = "Today",
                status = "Submitted Successfully"
            )
        )
    }

    private fun extractCleanLastDate(
        lastDateText: String?,
        postDate: String?,
        shortInfo: String?,
        title: String? = null,
        searchCorpus: String? = null
    ): String {
        val invalidValues = setOf("null", "none", "undefined", "govt vacancy", "latest", "released", "declared", "", "notice out")
        val startPhrases = Regex("(?i)\\b(?:link\\s+(?:will\\s+be\\s+|is\\s+)?activat(?:e|ed|ion)|link\\s+active|application\\s+begin|application\\s+start|form\\s+start|form\\s+begin|apply\\s+online\\s+start|apply\\s+start|starting\\s+date|opening\\s+date|start\\s+date|shuru|active\\s+soon|will\\s+be\\s+active|शुरू|प्रारंभ|प्रारम्भ|आरंभ|एक्टिवेट|शुरुआती)\\b")
        val explicitLastPhrases = Regex("(?i)\\b(?:last\\s+date|closing\\s+date|end\\s+date|expiry\\s+date|complete\\s+form|pay\\s+fee\\s+last|अंतिम\\s+तिथि|अन्तिम\\s+तिथि|अंतिम\\s+तारीख|आखिरी\\s+तारीख|समाप्ति\\s+तिथि)\\b")

        // Priority 1: Directly provided last_date_text from database/JSON (only if it is NOT a start-date phrase)
        if (!lastDateText.isNullOrBlank()) {
            val rawClean = lastDateText.replace(Regex("<[^>]*>"), "").trim()
            val lowerRaw = rawClean.lowercase()
            val isStartOnly = startPhrases.containsMatchIn(rawClean) && !explicitLastPhrases.containsMatchIn(rawClean)
            if (!isStartOnly && lowerRaw !in invalidValues && !lowerRaw.contains("null")) {
                val cleanStr = rawClean
                    .replace(Regex("(?i)^(?:Apply\\s+Online\\s+|Registration\\s+|Online\\s+Form\\s+)?Last\\s+Date\\s*:?\\s*"), "")
                    .replace(Regex("(?i)^Last\\s*:\\s*"), "")
                    .replace(Regex("(?i)^Extended\\s*:?\\s*|Extended\\s+Date\\s*:?\\s*"), "")
                    .trim()
                if (cleanStr.isNotBlank() && cleanStr.lowercase() !in invalidValues && !cleanStr.lowercase().contains("null")) {
                    return cleanStr
                }
            }
        }

        val contentToSearch = listOfNotNull(shortInfo, searchCorpus, title).joinToString(" ")
        if (contentToSearch.isNotBlank()) {
            // Priority 2: Use HtmlDateExtractor engine to parse full HTML tables, keywords, red/bold styling
            val extracted = HtmlDateExtractor.extractDatesFromHtml(contentToSearch)
            if (!extracted.lastDate.isNullOrBlank()) {
                val cleanLast = extracted.lastDate.lowercase()
                if (cleanLast !in invalidValues && !cleanLast.contains("null")) {
                    return extracted.lastDate
                }
            }

            // Priority 3: Extract from HTML Red Color / Bold tags (<font color="red"><b>...</b></font> or <span style="color:red">...</span> or <b><font color="red">...</font></b>)
            val redBoldPattern = Regex("(?i)(?:<font\\s+color=[\"']?(?:red|#f00|#ff0000|#d32f2f)[\"']?>|<span\\s+style=[\"']?[^\"']*color\\s*:\\s*(?:red|#f00|#ff0000|#d32f2f)[^\"']*[\"']?>|<b[^>]*>|<strong[^>]*>)\\s*(?:<b[^>]*>|<strong[^>]*>|<font\\s+color=[\"']?(?:red|#f00|#ff0000|#d32f2f)[\"']?>|<span\\s+style=[\"']?[^\"']*color\\s*:\\s*(?:red|#f00|#ff0000|#d32f2f)[^\"']*[\"']?>)?\\s*([0-9]{1,2}[/\\-\\s][0-9A-Za-z]{1,9}[/\\-\\s][0-9]{2,4}|[0-9]{1,2}\\s+[A-Za-z]{3,9}\\s+[0-9]{4}|Tomorrow|Closing Soon|Extended[^<]*)", RegexOption.IGNORE_CASE)
            redBoldPattern.find(contentToSearch)?.let {
                val dateStr = it.groupValues[1].replace(Regex("<[^>]*>"), "").trim()
                if (dateStr.isNotBlank() && dateStr.lowercase() !in invalidValues) return dateStr
            }

            // Priority 4: Keyword Table Cell Search (English & Hindi keywords) in <td> / <th> cells
            val tableRowPattern = Regex("(?i)(?:Last\\s+Date|Closing\\s+Date|Application\\s+Fee\\s+Last\\s+Date|Online\\s+End\\s+Date|Apply\\s+Online\\s+Last\\s+Date|Pay\\s+Exam\\s+Fee\\s+Last\\s+Date|Complete\\s+Form\\s+Last\\s+Date|अंतिम\\s+तिथि|अन्तिम\\s+तिथि|फॉर्म\\s+भरने\\s+की\\s+अंतिम\\s+तिथि|आवेदन\\s+करने\\s+की\\s+अंतिम\\s+तिथि|अंतिम\\s+तारीख)[^<]*</t[dh]>\\s*<t[dh][^>]*>(?:<[^>]+>)*\\s*([0-9]{1,2}[/\\-\\s][0-9A-Za-z]{1,9}[/\\-\\s][0-9]{2,4}|[0-9]{1,2}\\s+[A-Za-z]{3,9}\\s+[0-9]{4}|Tomorrow|Closing Soon|Extended[^<]*)")
            tableRowPattern.find(contentToSearch)?.let {
                val dateStr = it.groupValues[1].replace(Regex("<[^>]*>"), "").trim()
                if (dateStr.isNotBlank() && dateStr.lowercase() !in invalidValues) return dateStr
            }

            // Priority 5: Standard "Last Date : DD/MM/YYYY" or Hindi "अंतिम तिथि : DD/MM/YYYY" pattern
            val patternLastDateColon = Regex("(?i)(?:Last\\s+Date|Closing\\s+Date|Apply\\s+Last\\s+Date|Application\\s+Fee\\s+Last\\s+Date|Registration\\s+Last\\s+Date|Pay\\s+Exam\\s+Fee\\s+Last\\s+Date|अंतिम\\s+तिथि|अन्तिम\\s+तिथि|अंतिम\\s+तारीख)\\s*:?\\s*([0-9]{1,2}[/\\-\\s][0-9A-Za-z]{1,9}[/\\-\\s][0-9]{2,4}|[0-9]{1,2}\\s+[A-Za-z]{3,9}\\s+[0-9]{4}|Tomorrow|Closing Soon)")
            patternLastDateColon.find(contentToSearch)?.let {
                val dateStr = it.groupValues[1].replace(Regex("<[^>]*>"), "").trim()
                if (dateStr.isNotBlank() && dateStr.lowercase() !in invalidValues) return dateStr
            }

            // Priority 6: Any date like 11/05/2026 or 11-05-2026 or 11 May 2026
            val dateOnlyPattern = Regex("([0-9]{1,2}[/\\-][0-9]{1,2}[/\\-]20[2-9][0-9]|[0-9]{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+20[2-9][0-9])", RegexOption.IGNORE_CASE)
            dateOnlyPattern.find(contentToSearch)?.let {
                val dateStr = it.groupValues[1].trim()
                if (dateStr.isNotBlank()) return dateStr
            }
        }

        if (!postDate.isNullOrBlank()) {
            val trimmedPost = postDate.trim()
            if (trimmedPost.lowercase() !in invalidValues && !trimmedPost.lowercase().contains("null")) {
                val cleanPostDate = trimmedPost.split("|").firstOrNull()?.trim() ?: trimmedPost
                if (cleanPostDate.isNotBlank() && !cleanPostDate.lowercase().contains("null")) return cleanPostDate
            }
        }

        return "Notice Out"
    }

    private fun extractCleanPostDate(postDate: String?): String {
        if (postDate.isNullOrBlank()) return "Latest"
        val trimmed = postDate.trim()
        if (trimmed.lowercase() == "null" || trimmed.lowercase() == "none") return "Latest"
        val clean = trimmed.split("|").firstOrNull()?.trim() ?: trimmed
        return if (clean.isNotBlank() && !clean.lowercase().contains("null")) clean else "Latest"
    }
}
