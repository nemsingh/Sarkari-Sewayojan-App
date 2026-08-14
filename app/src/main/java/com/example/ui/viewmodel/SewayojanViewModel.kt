package com.example.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdmitCardItem
import com.example.data.local.ExamResultItem
import com.example.data.local.JobOpportunity
import com.example.data.local.SewayojanDatabase
import com.example.data.local.UserApplication
import com.example.data.repository.SewayojanRepository
import com.example.ui.screens.HomeCategoryLists
import com.example.ui.screens.PhotoCardData
import com.example.ui.screens.TopBannerCardData
import com.example.ui.screens.getHomeCategoryListsHelper
import com.example.ui.screens.getPhotoBannersHelper
import com.example.ui.screens.getTrendingJobsHelper
import com.example.ui.screens.getUrgentTopBanners
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "User" or "Sewayojan AI"
    val text: String,
    val time: String = "Just now"
)

class SewayojanViewModel(application: Application) : AndroidViewModel(application) {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("SewayojanVM", "Caught coroutine exception safely: ${throwable.message}")
    }

    private var lastRefreshTimestamp = 0L

    private val repository: SewayojanRepository
    val jobs: StateFlow<List<JobOpportunity>>
    val admitCards: StateFlow<List<AdmitCardItem>>
    val examResults: StateFlow<List<ExamResultItem>>
    val userApplications: StateFlow<List<UserApplication>>

    // Navigation & View States
    private val _selectedBottomTab = MutableStateFlow(0) // 0: Manage, 1: Jobs, 2: Admit/Results, 3: Ask Us
    val selectedBottomTab: StateFlow<Int> = _selectedBottomTab.asStateFlow()

    private val _homeCategoryFilter = MutableStateFlow("All")
    val homeCategoryFilter: StateFlow<String> = _homeCategoryFilter.asStateFlow()

    private val _jobsCategoryFilter = MutableStateFlow("All")
    val jobsCategoryFilter: StateFlow<String> = _jobsCategoryFilter.asStateFlow()

    private val _admitResultSubTab = MutableStateFlow(0) // 0: Admit Card, 1: Result
    val admitResultSubTab: StateFlow<Int> = _admitResultSubTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _isQrScannerOpen = MutableStateFlow(false)
    val isQrScannerOpen: StateFlow<Boolean> = _isQrScannerOpen.asStateFlow()

    private val _selectedJobForDetail = MutableStateFlow<JobOpportunity?>(null)
    val selectedJobForDetail: StateFlow<JobOpportunity?> = _selectedJobForDetail.asStateFlow()

    private val _activeServiceDialog = MutableStateFlow<String?>(null)
    val activeServiceDialog: StateFlow<String?> = _activeServiceDialog.asStateFlow()

    private val _selectedToolId = MutableStateFlow<String?>(null)
    val selectedToolId: StateFlow<String?> = _selectedToolId.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    // Chat AI state
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                sender = "Sewayojan AI",
                text = "Namaste! I am your Sewayojan Assistant. Ask me about government job eligibility, age limits, syllabus, admit card release dates, or how to apply for vacancies!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        val db = SewayojanDatabase.getDatabase(application)
        repository = SewayojanRepository(db.sewayojanDao())

        val prefs = application.getSharedPreferences("fcm_sync_prefs", android.content.Context.MODE_PRIVATE)
        val initialNotif = prefs.getBoolean("notifications_enabled", true)
        val denyCount = prefs.getInt("notif_deny_count", 0)
        _isNotificationEnabled.value = initialNotif

        // 1. Attach real-time Firestore snapshot listeners for instant updates
        repository.startRealtimeSync(viewModelScope)

        // 2. Initial sync on app start
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try { repository.initializeDefaultDataIfEmpty() } catch (_: Exception) {}
            try { repository.syncWithFirebase(forceFetchJson = false) } catch (_: Exception) {}
        }

        jobs = repository.allJobs.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )
        admitCards = repository.allAdmitCards.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )
        examResults = repository.allExamResults.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )
        userApplications = repository.userApplications.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptyList()
        )
    }

    // Filtered lists
    val filteredJobs: StateFlow<List<JobOpportunity>> = combine(
        jobs, jobsCategoryFilter, searchQuery
    ) { jobList, category, query ->
        jobList.filter { job ->
            val matchesCategory = (category == "All" || job.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() || job.title.contains(query, ignoreCase = true) || job.department.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredAdmitCards: StateFlow<List<AdmitCardItem>> = combine(
        admitCards, searchQuery
    ) { cardList, query ->
        cardList.filter { card ->
            query.isBlank() || card.examTitle.contains(query, ignoreCase = true) || card.department.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredResults: StateFlow<List<ExamResultItem>> = combine(
        examResults, searchQuery
    ) { resList, query ->
        resList.filter { res ->
            query.isBlank() || res.examTitle.contains(query, ignoreCase = true) || res.department.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Pre-computed HomeScreen Lists (Processed on Background Thread)
    val homeTopBanners: StateFlow<List<TopBannerCardData>> = jobs
        .map { jobList ->
            getUrgentTopBanners(jobList)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val homePhotoBanners: StateFlow<List<PhotoCardData>> = jobs
        .map { jobList ->
            getPhotoBannersHelper(jobList)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val homeTrendingJobs: StateFlow<List<JobOpportunity>> = jobs
        .map { jobList ->
            getTrendingJobsHelper(jobList)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val homeCategoryLists: StateFlow<HomeCategoryLists> = combine(jobs, admitCards, examResults) { jobList, admitList, resultList ->
        getHomeCategoryListsHelper(jobList, admitList, resultList)
    }
    .flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.Lazily, HomeCategoryLists())

    // Force instant background sync with debouncing (minimum 1s between triggers)
    fun refreshData() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTimestamp < 1_000L) return // Skip duplicate sync calls within 1s
        lastRefreshTimestamp = now
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                repository.syncWithFirebase(forceFetchJson = true)
            } catch (_: Exception) {}
        }
    }

    // Actions
    fun setBottomTab(index: Int) {
        _selectedBottomTab.value = index
        _homeCategoryFilter.value = "Home"
        _activeServiceDialog.value = null
        refreshData()
    }

    fun setHomeCategoryFilter(category: String) {
        _homeCategoryFilter.value = category
        _selectedBottomTab.value = 0
        if (category == "Home" || category == "All") {
            _activeServiceDialog.value = null
        } else {
            _activeServiceDialog.value = category
        }
        refreshData()
    }

    fun setJobsCategoryFilter(category: String) {
        _jobsCategoryFilter.value = category
        refreshData()
    }

    fun setAdmitResultSubTab(index: Int) {
        _admitResultSubTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) _searchQuery.value = ""
    }

    fun toggleDrawer(open: Boolean) {
        _isDrawerOpen.value = open
    }

    fun toggleQrScanner(open: Boolean) {
        _isQrScannerOpen.value = open
    }

    fun openJobDetail(job: JobOpportunity) {
        _selectedJobForDetail.value = job
    }

    fun openJobDetailFromNotification(
        postId: String = "",
        postUrl: String = "",
        title: String = "",
        category: String? = null,
        applyUrl: String? = null,
        description: String? = null
    ) {
        if (postId.isBlank() && postUrl.isBlank() && title.isBlank() && applyUrl.isNullOrBlank()) return

        android.util.Log.d("FCM_Notification", "Navigation to Job Detail started")

        val effectiveUrl = if (postUrl.isNotBlank()) postUrl else (applyUrl ?: "")
        val numericId = postId.trim().toIntOrNull()

        fun findMatchInList(currentJobs: List<JobOpportunity>): JobOpportunity? {
            var matchedJob: JobOpportunity? = null
            if (numericId != null) {
                matchedJob = currentJobs.find { it.id == numericId }
            }
            if (matchedJob == null && effectiveUrl.isNotBlank()) {
                matchedJob = currentJobs.find { job ->
                    job.applyUrl.contains(effectiveUrl, ignoreCase = true) || effectiveUrl.contains(job.applyUrl, ignoreCase = true)
                }
            }
            if (matchedJob == null && title.isNotBlank()) {
                matchedJob = currentJobs.find { job ->
                    job.title.trim().equals(title.trim(), ignoreCase = true)
                }
            }
            return matchedJob
        }

        val currentJobs = jobs.value
        var matchedJob = findMatchInList(currentJobs)

        // Check admit cards
        if (matchedJob == null && (category?.contains("Admit", ignoreCase = true) == true || title.contains("Admit", ignoreCase = true))) {
            val matchedAdmitCard = admitCards.value.find { card ->
                (numericId != null && card.id == numericId) ||
                (title.isNotBlank() && card.examTitle.trim().equals(title.trim(), ignoreCase = true))
            }
            if (matchedAdmitCard != null) {
                matchedJob = JobOpportunity(
                    title = matchedAdmitCard.examTitle,
                    category = "Admit Card",
                    department = matchedAdmitCard.department,
                    vacancies = "Admit Card Released",
                    dateText = matchedAdmitCard.examDateOrCenter,
                    statusTag = "ADMIT CARD",
                    description = "Admit Card for ${matchedAdmitCard.examTitle} issued by ${matchedAdmitCard.department}.",
                    applyUrl = matchedAdmitCard.downloadUrl
                )
            }
        }

        // Check exam results
        if (matchedJob == null && (category?.contains("Result", ignoreCase = true) == true || title.contains("Result", ignoreCase = true))) {
            val matchedResult = examResults.value.find { res ->
                (numericId != null && res.id == numericId) ||
                (title.isNotBlank() && res.examTitle.trim().equals(title.trim(), ignoreCase = true))
            }
            if (matchedResult != null) {
                matchedJob = JobOpportunity(
                    title = matchedResult.examTitle,
                    category = "Result",
                    department = matchedResult.department,
                    vacancies = "Result Announced",
                    dateText = matchedResult.releasedDate,
                    statusTag = "RESULT",
                    description = "Exam Result for ${matchedResult.examTitle} released by ${matchedResult.department}.",
                    applyUrl = matchedResult.resultUrl
                )
            }
        }

        // Synchronously populate selectedJobForDetail IMMEDIATELY before UI renders
        if (matchedJob != null) {
            _selectedJobForDetail.value = matchedJob
        } else {
            _selectedJobForDetail.value = JobOpportunity(
                title = if (title.isNotBlank()) title else "Job Update",
                category = category ?: "Latest Jobs",
                department = "Sarkari Sewayojan",
                vacancies = "Check Official Post Details",
                dateText = "Latest Update",
                statusTag = "NEW",
                description = description ?: "Tap apply button below to view official details on Sarkari Sewayojan.",
                applyUrl = effectiveUrl
            )
        }
        android.util.Log.d("FCM_Notification", "Job Detail opened successfully")

        // Asynchronously check if Room DB finishes sync later during cold start and update with richer data if found
        if (currentJobs.isEmpty()) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(200)
                val updatedJobs = jobs.value
                val betterMatch = findMatchInList(updatedJobs)
                if (betterMatch != null) {
                    _selectedJobForDetail.value = betterMatch
                }
            }
        }
    }

    fun closeJobDetail() {
        _selectedJobForDetail.value = null
    }

    fun openServiceDialog(serviceType: String) {
        _homeCategoryFilter.value = serviceType
        _selectedBottomTab.value = 0
        _activeServiceDialog.value = serviceType
    }

    fun openTool(toolId: String) {
        _activeServiceDialog.value = null
        _selectedToolId.value = toolId
        _selectedBottomTab.value = 2
    }

    fun clearSelectedTool() {
        _selectedToolId.value = null
    }

    fun closeServiceDialog() {
        _activeServiceDialog.value = null
        _homeCategoryFilter.value = "Home"
    }

    fun toggleBookmarkJob(job: JobOpportunity) {
        viewModelScope.launch {
            repository.toggleSaveJob(job)
            val currentDetail = _selectedJobForDetail.value
            if (currentDetail != null && currentDetail.title.trim().equals(job.title.trim(), ignoreCase = true)) {
                _selectedJobForDetail.value = currentDetail.copy(isSaved = !job.isSaved)
            }
            val msg = if (!job.isSaved) "Job saved to bookmarks" else "Removed from bookmarks"
            showToast(msg)
        }
    }

    fun toggleBookmarkAdmitCard(card: AdmitCardItem) {
        viewModelScope.launch {
            repository.toggleSaveAdmitCard(card)
            val msg = if (!card.isSaved) "Admit card saved" else "Removed from bookmarks"
            showToast(msg)
        }
    }

    fun applyForJob(title: String) {
        viewModelScope.launch {
            repository.submitApplication(title)
            showToast("Application submitted successfully for $title")
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage("User", userText))
        _chatMessages.value = current

        viewModelScope.launch {
            // Generate helpful automated response for government jobs
            val responseText = when {
                userText.contains("age", ignoreCase = true) ->
                    "For most Central & UP Govt jobs: General age limit is 18-30/40 years. OBC gets 3 years relaxation, SC/ST gets 5 years relaxation as per govt norms."
                userText.contains("rrb", ignoreCase = true) || userText.contains("railway", ignoreCase = true) ->
                    "Railway RRB Technician 2026 application is open! 6565+ vacancies available. Qualification: ITI / Diploma / Matriculation. Check the 'Jobs' tab to view details."
                userText.contains("police", ignoreCase = true) || userText.contains("up", ignoreCase = true) ->
                    "UP Police Constable results are out! Sub Inspector (SI) score cards are also available under the 'Results' section."
                userText.contains("admit card", ignoreCase = true) || userText.contains("hall ticket", ignoreCase = true) ->
                    "Active hall tickets: RRB ALP CBT-II, UPSC CMS 2026, IB MTS Tier-II, and SBI PO Prelims. Switch to the 'Admit Card' tab to download."
                else ->
                    "Thank you for reaching out! You can track all job updates, syllabus notifications, admit cards, and merit lists directly in Sewayojan. Use the 'Manage' grid to explore available services."
            }
            _chatMessages.value = _chatMessages.value + ChatMessage("Sewayojan AI", responseText)
        }
    }

    fun toggleNotifications() {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("fcm_sync_prefs", android.content.Context.MODE_PRIVATE)
        val newValue = !_isNotificationEnabled.value

        if (newValue) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                showToast("⚠️ Please allow notifications in Mobile Settings > Apps > Sarkari Sewayojan > Notifications")
            } else {
                _isNotificationEnabled.value = true
                prefs.edit().putBoolean("notifications_enabled", true).apply()
                showToast("🔔 Job Alerts Notifications ON")
                try {
                    com.example.data.service.MyFirebaseMessagingService.subscribeAllTopics(context)
                } catch (_: Exception) {}
            }
        } else {
            _isNotificationEnabled.value = false
            prefs.edit().putBoolean("notifications_enabled", false).apply()
            showToast("🔕 Notifications Turned OFF")
            try {
                com.example.data.service.MyFirebaseMessagingService.unsubscribeAllTopics()
            } catch (_: Exception) {}
        }
    }

    fun onNotifPermissionAllow() {
        val app = getApplication<Application>()
        val prefs = app.getSharedPreferences("fcm_sync_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("notifications_enabled", true)
            .apply()
        _isNotificationEnabled.value = true
        showToast("🔔 Job Alerts Notifications ON")
        try {
            com.example.data.service.MyFirebaseMessagingService.subscribeAllTopics(app)
        } catch (_: Exception) {}
    }

    fun onNotifPermissionDeny() {
        val prefs = getApplication<Application>().getSharedPreferences("fcm_sync_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("notifications_enabled", false)
            .apply()
        _isNotificationEnabled.value = false
        showToast("🔕 Notifications OFF")
        try {
            com.example.data.service.MyFirebaseMessagingService.unsubscribeAllTopics()
        } catch (_: Exception) {}
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
