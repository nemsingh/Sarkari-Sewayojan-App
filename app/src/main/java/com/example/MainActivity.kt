package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.viewmodel.SewayojanViewModel
import com.example.ui.components.FixedCategoryIconBar
import com.example.ui.components.GlobalSearchDialog
import com.example.ui.components.JobDetailBottomSheet
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.ServiceDialog
import com.example.ui.components.SewayojanBottomNavBar
import com.example.ui.components.SewayojanDrawerContent
import com.example.ui.components.SewayojanTopBar
import com.example.ui.screens.AdmitCardResultScreen
import com.example.ui.screens.AskUsScreen
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.CategoryScreen
import com.example.ui.screens.FollowUsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JobsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.SewayojanTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: SewayojanViewModel by viewModels()
    private var currentNotificationIntent by mutableStateOf<android.content.Intent?>(null)

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentNotificationIntent = intent
    }

    private fun handleNotificationIntent(intent: android.content.Intent?, onProcessed: (() -> Unit)? = null) {
        if (intent == null) return
        val openDetail = intent.getBooleanExtra("open_job_detail", false)
        val postId = intent.getStringExtra("post_id") ?: intent.getStringExtra("postId") ?: intent.getStringExtra("id") ?: intent.getStringExtra("slug") ?: ""
        val postUrl = intent.getStringExtra("post_url") ?: intent.getStringExtra("postUrl") ?: intent.getStringExtra("url") ?: intent.getStringExtra("applyUrl") ?: intent.getStringExtra("apply_url") ?: intent.getStringExtra("link") ?: ""
        val jobTitle = intent.getStringExtra("job_title")
            ?: intent.getStringExtra("jobTitle")
            ?: intent.getStringExtra("title")
            ?: intent.getStringExtra("gcm.n.title")
            ?: intent.getStringExtra("gcm.notification.title")
            ?: intent.getStringExtra("post_title")
            ?: intent.getStringExtra("name_of_post")
            ?: intent.getStringExtra("heading")
            ?: ""
        val category = intent.getStringExtra("category") ?: intent.getStringExtra("tag") ?: intent.getStringExtra("cat") ?: "Latest Jobs"
        val applyUrl = intent.getStringExtra("apply_url") ?: intent.getStringExtra("applyUrl") ?: postUrl
        val description = intent.getStringExtra("description")
            ?: intent.getStringExtra("body")
            ?: intent.getStringExtra("gcm.n.body")
            ?: intent.getStringExtra("gcm.notification.body")
            ?: intent.getStringExtra("message")
            ?: intent.getStringExtra("short_info")
        val isFcmNotification = intent.hasExtra("google.message_id") || intent.hasExtra("gcm.n.e") || intent.hasExtra("gcm.notification.e") || intent.hasExtra("from")

        if (openDetail || postId.isNotBlank() || postUrl.isNotBlank() || jobTitle.isNotBlank() || isFcmNotification) {
            android.util.Log.d("FCM_Notification", "MainActivity launched from notification: postId=$postId, postUrl=$postUrl, jobTitle=$jobTitle")

            viewModel.openJobDetailFromNotification(
                postId = postId,
                postUrl = postUrl,
                title = jobTitle,
                category = category,
                applyUrl = applyUrl,
                description = description
            )
            onProcessed?.invoke()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        // Initial notification setup
        try {
            com.example.data.service.MyFirebaseMessagingService.subscribeAllTopics()
        } catch (e: Exception) {
            android.util.Log.e("FCM_Setup", "FCM topic subscription error: ${e.message}")
        }

        // Set initial intent for lifecycle-safe LaunchedEffect processing in Compose
        currentNotificationIntent = intent

        setContent {
            SewayojanTheme {
                val context = LocalContext.current
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                // ViewModel States
                val selectedTab by viewModel.selectedBottomTab.collectAsStateWithLifecycle()
                val homeCategoryFilter by viewModel.homeCategoryFilter.collectAsStateWithLifecycle()
                val jobsCategoryFilter by viewModel.jobsCategoryFilter.collectAsStateWithLifecycle()
                val admitResultSubTab by viewModel.admitResultSubTab.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
                val isQrScannerOpen by viewModel.isQrScannerOpen.collectAsStateWithLifecycle()
                val selectedJobForDetail by viewModel.selectedJobForDetail.collectAsStateWithLifecycle()
                val activeServiceDialog by viewModel.activeServiceDialog.collectAsStateWithLifecycle()
                val selectedToolId by viewModel.selectedToolId.collectAsStateWithLifecycle()
                val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

                // Data Streams
                val jobs by viewModel.jobs.collectAsStateWithLifecycle()
                val admitCards by viewModel.admitCards.collectAsStateWithLifecycle()
                val examResults by viewModel.examResults.collectAsStateWithLifecycle()
                val filteredJobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
                val filteredAdmitCards by viewModel.filteredAdmitCards.collectAsStateWithLifecycle()
                val filteredResults by viewModel.filteredResults.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsStateWithLifecycle()

                val notifPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        viewModel.onNotifPermissionAllow()
                    } else {
                        viewModel.onNotifPermissionDeny()
                    }
                }

                // Initialize FCM topic subscriptions and prompt Android 13+ permission dialog if needed
                LaunchedEffect(Unit) {
                    com.example.data.service.MyFirebaseMessagingService.subscribeAllTopics(context)

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        val prefs = context.getSharedPreferences("fcm_sync_prefs", android.content.Context.MODE_PRIVATE)
                        val hasPrompted = prefs.getBoolean("has_prompted_system_notif", false)
                        val isGranted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        
                        if (!isGranted && !hasPrompted) {
                            prefs.edit().putBoolean("has_prompted_system_notif", true).apply()
                            notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Precomputed HomeScreen Streams
                val homeTopBanners by viewModel.homeTopBanners.collectAsStateWithLifecycle()
                val homePhotoBanners by viewModel.homePhotoBanners.collectAsStateWithLifecycle()
                val homeTrendingJobs by viewModel.homeTrendingJobs.collectAsStateWithLifecycle()
                val homeCategoryLists by viewModel.homeCategoryLists.collectAsStateWithLifecycle()

                // Handle Toast Messages
                LaunchedEffect(toastMessage) {
                    toastMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                val isNotifLaunch = remember(currentNotificationIntent) {
                    val currentIntent = currentNotificationIntent
                    if (currentIntent != null) {
                        val openDetail = currentIntent.getBooleanExtra("open_job_detail", false)
                        val postId = currentIntent.getStringExtra("post_id") ?: currentIntent.getStringExtra("postId") ?: currentIntent.getStringExtra("id") ?: ""
                        val postUrl = currentIntent.getStringExtra("post_url") ?: currentIntent.getStringExtra("postUrl") ?: currentIntent.getStringExtra("url") ?: ""
                        val jobTitle = currentIntent.getStringExtra("job_title") ?: currentIntent.getStringExtra("jobTitle") ?: currentIntent.getStringExtra("title") ?: ""
                        openDetail || postId.isNotBlank() || postUrl.isNotBlank() || jobTitle.isNotBlank() || currentIntent.hasExtra("google.message_id") || currentIntent.hasExtra("gcm.n.e") || currentIntent.hasExtra("gcm.notification.e")
                    } else false
                }

                var showSplash by remember { mutableStateOf(!isNotifLaunch) }
                var isNotifProcessing by remember(currentNotificationIntent) { mutableStateOf(isNotifLaunch) }

                // Lifecycle-safe notification intent handling after Compose & ViewModel are fully initialized
                LaunchedEffect(currentNotificationIntent) {
                    val activeIntent = currentNotificationIntent
                    if (activeIntent != null) {
                        handleNotificationIntent(activeIntent) {
                            showSplash = false
                        }
                    }
                }

                BackHandler(enabled = drawerState.isOpen) {
                    coroutineScope.launch { drawerState.close() }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = !showSplash && !isNotifProcessing && selectedJobForDetail == null,
                    drawerContent = {
                        SewayojanDrawerContent(
                            onNavigateTab = { tabIndex ->
                                viewModel.setBottomTab(tabIndex)
                            },
                            onCloseDrawer = {
                                coroutineScope.launch { drawerState.close() }
                            },
                            isNotificationEnabled = isNotificationEnabled,
                            onToggleNotification = {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                                    context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.toggleNotifications()
                                }
                            },
                            onOpenServiceDialog = { service ->
                                viewModel.openServiceDialog(service)
                            },
                            onOpenTool = { toolId ->
                                viewModel.openTool(toolId)
                            },
                            onShowToast = { viewModel.showToast(it) }
                        )
                    }
                ) {
                    if (showSplash && !isNotifProcessing) {
                        SplashScreen(
                            onSplashFinished = {
                                showSplash = false
                            }
                        )
                    } else if (isNotifProcessing || selectedJobForDetail != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            val currentJob = selectedJobForDetail
                            if (currentJob != null) {
                                JobDetailBottomSheet(
                                    job = currentJob,
                                    sheetState = sheetState,
                                    onDismiss = {
                                        viewModel.closeJobDetail()
                                        isNotifProcessing = false
                                        showSplash = false
                                    },
                                    onToggleBookmark = { viewModel.toggleBookmarkJob(it) },
                                    onApply = { title -> viewModel.applyForJob(title) }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = PrimaryRed,
                                            modifier = Modifier.size(44.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Opening Post Details...",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Please wait...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = SecondaryGray
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                        topBar = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                SewayojanTopBar(
                                    title = when (selectedTab) {
                                        1 -> "Job Categories"
                                        2 -> "Candidate Tools"
                                        3 -> "Follow Us"
                                        else -> "Sarkari Sewayojan"
                                    },
                                    isNotificationEnabled = isNotificationEnabled,
                                    onMenuClick = {
                                        coroutineScope.launch { drawerState.open() }
                                    },
                                    onSearchClick = {
                                        viewModel.toggleSearchActive(true)
                                    },
                                    onNotificationToggle = {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                                            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            viewModel.toggleNotifications()
                                        }
                                    },
                                    onQrScanClick = {
                                        viewModel.toggleQrScanner(true)
                                    }
                                )
                                FixedCategoryIconBar(
                                    selectedCategory = homeCategoryFilter,
                                    onCategorySelect = { category ->
                                        viewModel.setHomeCategoryFilter(category)
                                    }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                            }
                        },
                        bottomBar = {
                            SewayojanBottomNavBar(
                                selectedIndex = selectedTab,
                                onTabSelected = { viewModel.setBottomTab(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = innerPadding.calculateTopPadding())
                        ) {
                            AnimatedContent(
                                targetState = activeServiceDialog ?: "TAB_$selectedTab",
                                transitionSpec = { 
                                    fadeIn(animationSpec = androidx.compose.animation.core.tween(120)) togetherWith 
                                    fadeOut(animationSpec = androidx.compose.animation.core.tween(120)) 
                                },
                                label = "ScreenTransition"
                            ) { screenKey ->
                                if (screenKey.startsWith("TAB_")) {
                                    val tab = screenKey.removePrefix("TAB_").toIntOrNull() ?: 0
                                    when (tab) {
                                        0 -> HomeScreen(
                                            selectedCategory = homeCategoryFilter,
                                            searchQuery = searchQuery,
                                            allJobs = jobs,
                                            admitCards = admitCards,
                                            examResults = examResults,
                                            topBanners = homeTopBanners,
                                            photoBanners = homePhotoBanners,
                                            trendingJobs = homeTrendingJobs,
                                            homeCategoryLists = homeCategoryLists,
                                            onCategorySelect = { viewModel.setHomeCategoryFilter(it) },
                                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                            onTriggerSearch = { query ->
                                                viewModel.setSearchQuery(query)
                                                viewModel.toggleSearchActive(true)
                                            },
                                            onJobSelect = { viewModel.openJobDetail(it) },
                                            onNavigateTab = { viewModel.setBottomTab(it) },
                                            onOpenServiceDialog = { viewModel.openServiceDialog(it) },
                                            onShowToast = { viewModel.showToast(it) }
                                        )

                                        1 -> CategoryScreen(
                                            allJobs = jobs,
                                            admitCards = admitCards,
                                            examResults = examResults,
                                            onCategoryClick = { category ->
                                                when (category) {
                                                    "Latest Jobs" -> viewModel.openServiceDialog("Latest Jobs")
                                                    "Admit Card" -> viewModel.openServiceDialog("Admit Card")
                                                    "Result" -> viewModel.openServiceDialog("Results")
                                                    else -> viewModel.openServiceDialog(category)
                                                }
                                            },
                                            onJobSelect = { viewModel.openJobDetail(it) },
                                            onShowToast = { viewModel.showToast(it) }
                                        )

                                        2 -> ToolsScreen(
                                            initialToolId = selectedToolId,
                                            onToolCleared = { viewModel.clearSelectedTool() },
                                            onShowToast = { viewModel.showToast(it) }
                                        )

                                        3 -> FollowUsScreen(
                                            onShowToast = { viewModel.showToast(it) }
                                        )
                                    }
                                } else {
                                    BackHandler {
                                        viewModel.closeServiceDialog()
                                    }
                                     val isInfoService = screenKey in listOf(
                                        "Help & Support", "Help", "Support",
                                        "About Sewayojan", "About Us",
                                        "Recharge", "Pay Bills",
                                        "OTR Details", "OTR (One Time Registration)", "OTR",
                                        "Saved Jobs", "Saved Posts", "Saved"
                                    )
                                    if (isInfoService) {
                                        ServiceDialog(
                                            serviceType = screenKey,
                                            allJobs = jobs,
                                            admitCards = admitCards,
                                            examResults = examResults,
                                            onDismiss = { viewModel.closeServiceDialog() },
                                            onJobSelect = { viewModel.openJobDetail(it) },
                                            onShowToast = { viewModel.showToast(it) },
                                            onToggleBookmark = { viewModel.toggleBookmarkJob(it) }
                                        )
                                    } else {
                                        CategoryDetailScreen(
                                            categoryTitle = screenKey,
                                            allJobs = jobs,
                                            onBack = { viewModel.closeServiceDialog() },
                                            onJobSelect = { viewModel.openJobDetail(it) },
                                            onShowToast = { viewModel.showToast(it) }
                                        )
                                    }
                                }
                            }

                            // Global Search Dialog Modal
                            if (isSearchActive) {
                                GlobalSearchDialog(
                                    initialQuery = searchQuery,
                                    allJobs = jobs,
                                    admitCards = admitCards,
                                    examResults = examResults,
                                    onDismiss = { viewModel.toggleSearchActive(false) },
                                    onJobSelect = { job ->
                                        viewModel.openJobDetail(job)
                                    },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                            }

                            // QR Scanner Simulator Modal
                            if (isQrScannerOpen) {
                                QrScannerDialog(
                                    onDismiss = { viewModel.toggleQrScanner(false) },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}
