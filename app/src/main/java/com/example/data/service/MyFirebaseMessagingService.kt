package com.example.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.repository.SewayojanRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val dataMap = remoteMessage.data
        val notificationObj = remoteMessage.notification

        // Extract metadata and payload parameters from any admin panel schema
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
            ?: dataMap["alert_title"]
            ?: dataMap["headline"]
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
            ?: dataMap["details"]
            ?: dataMap["text"]
            ?: dataMap["summary"]
            ?: ""

        val category = dataMap["category"] ?: dataMap["tag"] ?: dataMap["cat"] ?: dataMap["section"] ?: "Latest Jobs"
        val postId = dataMap["postId"] ?: dataMap["post_id"] ?: dataMap["id"] ?: dataMap["slug"] ?: ""
        val postUrl = dataMap["postUrl"] ?: dataMap["post_url"] ?: dataMap["url"] ?: dataMap["applyUrl"] ?: dataMap["apply_url"] ?: dataMap["link"] ?: ""
        val applyUrl = postUrl.ifBlank { dataMap["applyUrl"] ?: dataMap["post_slug"] ?: dataMap["apply_url"] ?: dataMap["url"] ?: dataMap["job_id"] ?: dataMap["slug"] ?: dataMap["link"] ?: "" }
        val specificJobTitle = dataMap["jobTitle"] ?: dataMap["job_title"] ?: dataMap["post_title"] ?: (if (notificationObj?.body != null && notificationObj.body!!.isNotBlank()) notificationObj.body!! else rawTitle)
        val imageUrl = dataMap["image"] ?: dataMap["imageUrl"] ?: dataMap["image_url"] ?: dataMap["picture"] ?: dataMap["banner"] ?: dataMap["photo"] ?: notificationObj?.imageUrl?.toString() ?: ""

        Log.d(TAG, "FCM Payload -> title: '$rawTitle', body: '$rawBody', category: '$category', postId: '$postId', postUrl: '$postUrl', applyUrl: '$applyUrl', imageUrl: '$imageUrl'")

        // Step 1: Always trigger live sync to refresh Room Database with new post/job
        serviceScope.launch {
            try {
                val repository = SewayojanRepository.getInstance(applicationContext)
                repository.syncWithFirebase(forceFetchJson = true)
                Log.d(TAG, "Background sync triggered successfully upon FCM signal.")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing data after FCM message: ${e.message}")
            }
        }

        // Step 2: Check System Permission & App Preference before showing Status Bar Notification
        val prefs = applicationContext.getSharedPreferences("fcm_sync_prefs", Context.MODE_PRIVATE)
        val isNotificationEnabled = prefs.getBoolean("notifications_enabled", true)

        val isSystemPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (isNotificationEnabled && isSystemPermissionGranted) {
            serviceScope.launch {
                showStatusBarNotification(
                    context = applicationContext,
                    title = rawTitle,
                    body = rawBody,
                    category = category,
                    applyUrl = applyUrl,
                    jobTitle = specificJobTitle,
                    postId = postId,
                    postUrl = postUrl,
                    imageUrl = imageUrl
                )
            }
        } else {
            Log.d(TAG, "Status bar notification suppressed. App pref: $isNotificationEnabled, OS Perm: $isSystemPermissionGranted")
        }
    }

    private suspend fun showStatusBarNotification(
        context: Context,
        title: String,
        body: String,
        category: String,
        applyUrl: String,
        jobTitle: String,
        postId: String = "",
        postUrl: String = "",
        imageUrl: String = ""
    ) = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create High Importance Notification Channel with Sound and Vibration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sarkari Sewayojan Job Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live notifications for Sarkari Result, Admit Card, Answer Key, and Latest Jobs"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
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

        val finalBody = if (body.isNotBlank()) body else "👉 Click here to check full details & apply online."
        val intentJobTitle = if (jobTitle.isNotBlank()) jobTitle else if (body.isNotBlank()) body else finalTitle

        // Deep Link Intent to launch MainActivity and open exact job detail modal
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

        // Try downloading image bitmap if provided in payload
        var bannerBitmap: Bitmap? = null
        if (imageUrl.isNotBlank()) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                val input = connection.inputStream
                bannerBitmap = BitmapFactory.decodeStream(input)
                connection.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "Could not download notification image: ${e.message}")
            }
        }

        val appIconBitmap = try {
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        } catch (_: Exception) {
            null
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(finalTitle)
            .setContentText(finalBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 100, 250))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        if (appIconBitmap != null) {
            notificationBuilder.setLargeIcon(appIconBitmap)
        }

        if (bannerBitmap != null) {
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bannerBitmap)
                    .setSummaryText(finalBody)
            )
        } else {
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(finalBody)
                    .setBigContentTitle(finalTitle)
            )
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d(TAG, "Status Bar Notification posted successfully! ID: $notificationId")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token: $token")
        subscribeAllTopics(applicationContext)
    }

    companion object {
        private const val TAG = "FCM_Notification"
        private const val CHANNEL_ID = "sewayojan_job_notifications"

        // All standard topics used across Sarkari Sewayojan Admin Panel
        val SUBSCRIBED_TOPICS = listOf(
            "all_users",
            "all_jobs",
            "all",
            "jobs",
            "posts",
            "data_updates",
            "latest_jobs",
            "admit_cards",
            "results",
            "notifications",
            "updates",
            "news",
            "sewayojan",
            "sewayojan_all"
        )

        fun subscribeAllTopics(context: Context? = null) {
            try {
                val fcm = FirebaseMessaging.getInstance()
                for (topic in SUBSCRIBED_TOPICS) {
                    fcm.subscribeToTopic(topic)
                        .addOnSuccessListener {
                            Log.d(TAG, "FCM successfully subscribed to topic: $topic")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "FCM failed subscribing to topic '$topic': ${e.message}")
                        }
                }
                Log.d(TAG, "All Admin Panel FCM topics subscription initialized.")
            } catch (e: Exception) {
                Log.e(TAG, "FCM topics subscription failed: ${e.message}")
            }
        }

        fun unsubscribeAllTopics() {
            try {
                val fcm = FirebaseMessaging.getInstance()
                for (topic in SUBSCRIBED_TOPICS) {
                    fcm.unsubscribeFromTopic(topic)
                }
                Log.d(TAG, "Unsubscribed from all FCM topics.")
            } catch (e: Exception) {
                Log.e(TAG, "FCM topic unsubscription failed: ${e.message}")
            }
        }
    }
}
