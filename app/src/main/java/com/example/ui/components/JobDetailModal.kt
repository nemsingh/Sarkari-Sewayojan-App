package com.example.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.local.JobOpportunity
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.SecondaryGray
import com.example.ui.theme.SurfaceContainerHigh

class ObservableWebView(context: android.content.Context) : WebView(context) {
    var onScrollEndListener: ((Boolean) -> Unit)? = null

    fun checkScrollPosition() {
        try {
            if (parent != null) {
                val range = computeVerticalScrollRange()
                val extent = computeVerticalScrollExtent()
                val offset = computeVerticalScrollOffset()
                val isAtEnd = (offset + extent) >= (range - 80) || range <= extent
                onScrollEndListener?.invoke(isAtEnd)
            }
        } catch (_: Exception) {}
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        checkScrollPosition()
    }
}

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
private const val MOBILE_USER_AGENT =
    "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailBottomSheet(
    job: JobOpportunity,
    sheetState: SheetState? = null,
    onDismiss: () -> Unit,
    onToggleBookmark: (JobOpportunity) -> Unit,
    onApply: (String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val targetDesktopWidth = 1024
    val initialScalePercent = ((screenWidthDp.toFloat() / targetDesktopWidth.toFloat()) * 100).toInt().coerceIn(15, 100)

    var isScrolledToBottom by remember { mutableStateOf(false) }

    val fullPostUrl = remember(job) {
        val url = job.applyUrl.trim()
        when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.isNotBlank() -> "https://sarkarisewayojan.com${if (url.startsWith("/")) "" else "/"}$url"
            else -> {
                val slug = job.title.lowercase()
                    .replace("[^a-z0-9]+".toRegex(), "-")
                    .trim('-')
                "https://sarkarisewayojan.com/post/$slug"
            }
        }
    }

    var isDesktopSite by remember { mutableStateOf(true) } // Enabled by default
    var isLoadingWeb by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    var dotCount by remember { mutableIntStateOf(1) }
    LaunchedEffect(isLoadingWeb) {
        if (isLoadingWeb) {
            while (isActive) {
                delay(400)
                dotCount = if (dotCount >= 3) 1 else dotCount + 1
            }
        }
    }
    val animatedDots = ".".repeat(dotCount)

    BackHandler {
        try {
            val wv = webViewRef
            if (wv != null && wv.canGoBack()) {
                wv.goBack()
            } else {
                onDismiss()
            }
        } catch (_: Exception) {
            onDismiss()
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            try {
                val wv = webViewRef
                webViewRef = null
                wv?.stopLoading()
                wv?.onPause()
            } catch (_: Exception) {}
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Top Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHigh)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = job.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (job.category.isNotBlank()) job.category else "Sarkari Sewayojan Notification",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Desktop Site Toggle Chip
                            Surface(
                                onClick = {
                                    isDesktopSite = !isDesktopSite
                                    webViewRef?.let { webView ->
                                        webView.settings.userAgentString =
                                            if (isDesktopSite) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                                        webView.settings.useWideViewPort = isDesktopSite
                                        webView.settings.loadWithOverviewMode = isDesktopSite
                                        if (isDesktopSite) {
                                            webView.setInitialScale(initialScalePercent)
                                        } else {
                                            webView.setInitialScale(0)
                                        }
                                        isLoadingWeb = true
                                        webView.reload()
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isDesktopSite) PrimaryRed else SurfaceContainerHigh,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDesktopSite) Icons.Default.DesktopWindows else Icons.Default.Smartphone,
                                        contentDescription = "Desktop Site Mode",
                                        modifier = Modifier.size(15.dp),
                                        tint = if (isDesktopSite) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isDesktopSite) "Desktop Site" else "Mobile Site",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isDesktopSite) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isDesktopSite) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            modifier = Modifier.size(13.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { onToggleBookmark(job) }
                            ) {
                                Icon(
                                    imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark Job",
                                    tint = if (job.isSaved) PrimaryRed else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = { webViewRef?.reload() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Page",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = {
                                    try {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, job.title)
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "${job.title}\nFull Notification & Apply: $fullPostUrl"
                                            )
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        val chooserIntent = Intent.createChooser(shareIntent, "Share Job Notification").apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(chooserIntent)
                                    } catch (e: Exception) {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Job Link", fullPostUrl)
                                        clipboard?.setPrimaryClip(clip)
                                        android.widget.Toast.makeText(context, "Job link copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Native Android WebView with Desktop Auto-Fit scale
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { ctx ->
                            ObservableWebView(ctx).apply {
                                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                                overScrollMode = android.view.View.OVER_SCROLL_ALWAYS
                                isVerticalScrollBarEnabled = true
                                isHorizontalScrollBarEnabled = true
                                isNestedScrollingEnabled = false

                                onScrollEndListener = { isEnd ->
                                    isScrolledToBottom = isEnd
                                }
                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        super.onProgressChanged(view, newProgress)
                                        if (newProgress >= 5) {
                                            injectViewportScript(view, isDesktopSite, screenWidthDp)
                                        }
                                        (view as? ObservableWebView)?.checkScrollPosition()
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(
                                        view: WebView?,
                                        url: String?,
                                        favicon: Bitmap?
                                    ) {
                                        super.onPageStarted(view, url, favicon)
                                        isLoadingWeb = true
                                        canGoBack = view?.canGoBack() == true
                                        canGoForward = view?.canGoForward() == true
                                        injectViewportScript(view, isDesktopSite, screenWidthDp)
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isLoadingWeb = false
                                        canGoBack = view?.canGoBack() == true
                                        canGoForward = view?.canGoForward() == true
                                        injectViewportScript(view, isDesktopSite, screenWidthDp)
                                        view?.postDelayed({
                                            (view as? ObservableWebView)?.checkScrollPosition()
                                        }, 300)
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val reqUrl = request?.url?.toString() ?: ""
                                        if (reqUrl.startsWith("http://") || reqUrl.startsWith("https://")) {
                                            if (reqUrl.contains("sarkarisewayojan.com") || reqUrl.contains("sewayojan")) {
                                                return false
                                            } else {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl))
                                                    ctx.startActivity(intent)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                                return true
                                            }
                                        }
                                        return false
                                    }
                                }

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    useWideViewPort = isDesktopSite
                                    loadWithOverviewMode = isDesktopSite
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    textZoom = 100
                                    userAgentString =
                                        if (isDesktopSite) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                                    
                                    setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
                                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                                    offscreenPreRaster = true
                                }

                                if (isDesktopSite) {
                                    setInitialScale(initialScalePercent)
                                } else {
                                    setInitialScale(0)
                                }

                                webViewRef = this
                                loadUrl(fullPostUrl)
                            }
                        },
                        update = { webView ->
                            webViewRef = webView
                            val targetUA = if (isDesktopSite) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                            if (webView.settings.userAgentString != targetUA) {
                                webView.settings.userAgentString = targetUA
                                webView.settings.useWideViewPort = isDesktopSite
                                webView.settings.loadWithOverviewMode = isDesktopSite
                                if (isDesktopSite) {
                                    webView.setInitialScale(initialScalePercent)
                                } else {
                                    webView.setInitialScale(0)
                                }
                                webView.reload()
                            }
                            (webView as? ObservableWebView)?.checkScrollPosition()
                        },
                        onRelease = { webView ->
                            try {
                                webViewRef = null
                                (webView.parent as? android.view.ViewGroup)?.removeView(webView)
                                webView.stopLoading()
                                webView.onPause()
                                webView.clearHistory()
                                webView.loadUrl("about:blank")
                                webView.webViewClient = object : WebViewClient() {}
                                webView.webChromeClient = object : WebChromeClient() {}
                                webView.removeAllViews()
                                webView.destroy()
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isLoadingWeb) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryRed)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Loading Please Wait$animatedDots",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimaryRed
                                )
                                Text(
                                    text = "sarkarisewayojan.com",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SecondaryGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Persistent Bottom Action Bar - Fixed layout height prevents WebView size jumps
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onToggleBookmark(job) },
                            shape = CircleShape,
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(
                                imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (job.isSaved) PrimaryRed else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (job.isSaved) "Saved" else "Save",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (job.isSaved) PrimaryRed else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPostUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    onApply(job.title)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply / Official Website",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

fun injectViewportScript(webView: WebView?, isDesktop: Boolean, screenWidthDp: Int) {
    if (webView == null) return
    val js = if (isDesktop) {
        val targetWidth = 1024
        val calcScale = (screenWidthDp.toFloat() / targetWidth.toFloat()).coerceIn(0.15f, 1.0f)
        val scaleStr = String.format(java.util.Locale.US, "%.3f", calcScale)
        """
        (function() {
            try {
                var metas = document.getElementsByTagName('meta');
                for (var i = metas.length - 1; i >= 0; i--) {
                    if (metas[i].name === 'viewport') {
                        metas[i].parentNode.removeChild(metas[i]);
                    }
                }
                var meta = document.createElement('meta');
                meta.name = 'viewport';
                meta.content = 'width=$targetWidth, initial-scale=$scaleStr, minimum-scale=0.1, maximum-scale=5.0, user-scalable=yes';
                if (document.head) {
                    document.head.appendChild(meta);
                } else if (document.documentElement) {
                    document.documentElement.appendChild(meta);
                }
                
                if (document.documentElement) {
                    document.documentElement.style.minWidth = '${targetWidth}px';
                    document.documentElement.style.width = '${targetWidth}px';
                    document.documentElement.style.overflowX = 'auto';
                }
                if (document.body) {
                    document.body.style.minWidth = '${targetWidth}px';
                    document.body.style.width = '${targetWidth}px';
                    document.body.style.overflowX = 'auto';
                }
            } catch(e) {}
        })();
        """.trimIndent()
    } else {
        """
        (function() {
            try {
                var metas = document.getElementsByTagName('meta');
                for (var i = metas.length - 1; i >= 0; i--) {
                    if (metas[i].name === 'viewport') {
                        metas[i].parentNode.removeChild(metas[i]);
                    }
                }
                var meta = document.createElement('meta');
                meta.name = 'viewport';
                meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes';
                if (document.head) {
                    document.head.appendChild(meta);
                }
                if (document.documentElement) {
                    document.documentElement.style.minWidth = '100%';
                    document.documentElement.style.width = '100%';
                }
                if (document.body) {
                    document.body.style.minWidth = '100%';
                    document.body.style.width = '100%';
                }
            } catch(e) {}
        })();
        """.trimIndent()
    }
    webView.evaluateJavascript(js, null)
}

