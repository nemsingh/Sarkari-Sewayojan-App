package com.example.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import com.example.MainActivity
import com.example.data.repository.SewayojanRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val dataMap = remoteMessage.data
        val notificationObj = remoteMessage.notification

        // Extract metadata and payload parameters
        val type = dataMap["type"] ?: dataMap["action"] ?: "DATA_UPDATED"
        val updateId = dataMap["update_id"] ?: dataMap["data_version"] ?: dataMap["version"] ?: ""

        val rawTitle = notificationObj?.title
            ?: dataMap["title"]
            ?: dataMap["jobTitle"]
            ?: dataMap["job_title"]
            ?: dataMap["post_title"]
            ?: dataMap["heading"]
            ?: dataMap["subject"]
            ?: dataMap["name_of_post"]
            ?: dataMap["post_name"]
            ?: dataMap["title_hi"]
            ?: dataMap["name"]
            ?: ""

        val rawBody = notificationObj?.body
            ?: dataMap["body"]
            ?: dataMap["jobTitle"]
            ?: dataMap["description"]
            ?: dataMap["message"]
            ?: dataMap["sub_title"]
            ?: dataMap["short_info"]
            ?: dataMap["content"]
            ?: dataMap["info"]
            ?: dataMap["msg"]
            ?: ""

        val category = dataMap["category"] ?: dataMap["tag"] ?: dataMap["cat"] ?: "Latest Jobs"
        val postId = dataMap["postId"] ?: dataMap["post_id"] ?: dataMap["id"] ?: ""
        val postUrl = dataMap["postUrl"] ?: dataMap["post_url"] ?: dataMap["url"] ?: dataMap["applyUrl"] ?: dataMap["apply_url"] ?: ""
        val applyUrl = postUrl.ifBlank { dataMap["applyUrl"] ?: dataMap["post_slug"] ?: dataMap["apply_url"] ?: dataMap["url"] ?: dataMap["job_id"] ?: dataMap["slug"] ?: dataMap["link"] ?: "" }
        val specificJobTitle = dataMap["jobTitle"] ?: dataMap["job_title"] ?: dataMap["post_title"] ?: (if (notificationObj?.body != null && notificationObj.body!!.isNotBlank()) notificationObj.body!! else rawTitle)

        Log.d(TAG, "FCM Payload -> title: '$rawTitle', body: '$rawBody', category: '$category', postId: '$postId', postUrl: '$postUrl', applyUrl: '$applyUrl', specificJobTitle: '$specificJobTitle', updateId: '$updateId'")

        // Deduplication check: Avoid downloading JSON multiple times for the same update_id
        val prefs = applicationContext.getSharedPreferences("fcm_sync_prefs", Context.MODE_PRIVATE)
        val lastProcessedVersion = prefs.getString("last_processed_version", "")

        val isDuplicate = !updateId.isNullOrBlank() && updateId == lastProcessedVersion

        if (!updateId.isNullOrBlank()) {
            prefs.edit().putString("last_processed_version", updateId).apply()
        }

        // Step 1: Trigger Vercel JSON sync into Room Database in background
        if (!isDuplicate) {
            Log.d(TAG, "New FCM update signal verified! Syncing Vercel JSON into Room DB...")
            val repository = SewayojanRepository.getInstance(applicationContext)
            serviceScope.launch {
                try {
                    repository.syncWithFirebase(forceFetchJson = true)
                    Log.d(TAG, "Vercel JSON successfully updated in Room DB. ZERO Firestore reads used.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing Vercel JSON after FCM signal: ${e.message}")
                }
            }
        }

        // Step 2: Check System Permission & App Pref before showing Status Bar Notification
        val isNotificationEnabled = prefs.getBoolean("notifications_enabled", true)
        
        // System level Android 13+ permission check is the final authority
        val isSystemPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (isNotificationEnabled && isSystemPermissionGranted) {
            showStatusBarNotification(
                context = applicationContext,
                title = rawTitle,
                body = rawBody,
                category = category,
                applyUrl = applyUrl,
                jobTitle = specificJobTitle,
                postId = postId,
                postUrl = postUrl
            )
        } else {
            Log.d(TAG, "Status bar notification suppressed. App pref enabled: $isNotificationEnabled, OS Permission granted: $isSystemPermissionGranted")
        }
    }

    private fun showStatusBarNotification(
        context: Context,
        title: String,
        body: String,
        category: String,
        applyUrl: String,
        jobTitle: String,
        postId: String = "",
        postUrl: String = ""
    ) {
        // Double safety: Verify system permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Cannot post notification: POST_NOTIFICATIONS permission not granted by OS.")
                return
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create High Importance Notification Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sarkari Sewayojan Job Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new sarkari result, admit card, and job updates"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val finalTitle = when {
            title.isNotBlank() -> title
            category.contains("Admit", ignoreCase = true) -> "📢 New Update: Admit Card"
            category.contains("Result", ignoreCase = true) -> "📢 New Update: Sarkari Result"
            category.contains("Answer", ignoreCase = true) -> "📢 New Update: Answer Key"
            category.contains("Syllabus", ignoreCase = true) -> "📢 New Update: Syllabus"
            else -> "📢 New Update: Latest Jobs"
        }

        val finalBody = if (body.isNotBlank()) body else "👉 Click Here to view detail"
        val intentJobTitle = if (jobTitle.isNotBlank()) jobTitle else if (body.isNotBlank()) body else finalTitle

        // Deep Link Intent to launch MainActivity and open exact job detail
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_job_detail", true)
            putExtra("post_id", postId)
            putExtra("postId", postId)
            putExtra("post_url", postUrl)
            putExtra("postUrl", postUrl)
            putExtra("job_title", intentJobTitle)
            putExtra("jobTitle", intentJobTitle)
            putExtra("category", category)
            putExtra("apply_url", applyUrl)
            putExtra("applyUrl", applyUrl)
            putExtra("description", body)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            pendingIntentFlags
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.example.R.mipmap.ic_launcher)
            .setContentTitle(finalTitle)
            .setContentText(finalBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(finalBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d(TAG, "System Notification posted on Status Bar! Notification ID: $notificationId")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token generated: $token")
        // Subscribe to 'all_users' ONLY if notification permission is granted by OS
        subscribeAllTopics(applicationContext)
    }

    companion object {
        private const val TAG = "FCM_Sync_Service"
        private const val CHANNEL_ID = "sewayojan_job_notifications"

        fun subscribeAllTopics(context: Context? = null) {
            try {
                if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    
                    if (!hasPermission) {
                        Log.w(TAG, "Skipping FCM 'all_users' subscription: POST_NOTIFICATIONS permission not granted.")
                        return
                    }
                }

                val fcm = com.google.firebase.messaging.FirebaseMessaging.getInstance()
                fcm.subscribeToTopic("all_users")
                Log.d(TAG, "Subscribed to FCM 'all_users' topic successfully!")
            } catch (e: Exception) {
                Log.e(TAG, "FCM topic subscription failed: ${e.message}")
            }
        }

        fun unsubscribeAllTopics() {
            try {
                val fcm = com.google.firebase.messaging.FirebaseMessaging.getInstance()
                fcm.unsubscribeFromTopic("all_users")
                // Also clean up any legacy topic subscriptions
                val legacyTopics = listOf("all_jobs", "all", "data_updates", "jobs", "posts", "latest_jobs", "admit_cards", "results", "notifications", "news", "updates")
                for (topic in legacyTopics) {
                    fcm.unsubscribeFromTopic(topic)
                }
                Log.d(TAG, "Unsubscribed from FCM topics!")
            } catch (e: Exception) {
                Log.e(TAG, "FCM topic unsubscription failed: ${e.message}")
            }
        }
    }
}
