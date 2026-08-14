package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.data.service.MyFirebaseMessagingService
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class SewayojanApp : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            // Initialize Firebase App
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "FirebaseApp initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseApp initialization error: ${e.message}")
        }

        // Create high priority notification channels early on process start
        createNotificationChannels()

        // Subscribe to FCM push topics immediately
        initFcmSubscriptions()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val notificationManager = getSystemService(NotificationManager::class.java)

                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()

                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Sarkari Sewayojan Job Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Instant notifications for Latest Jobs, Admit Card, Sarkari Result, and Exam Updates"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 100, 250)
                    enableLights(true)
                    setShowBadge(true)
                    setSound(soundUri, audioAttributes)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }

                notificationManager?.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel '$NOTIFICATION_CHANNEL_ID' created successfully with High Importance.")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating notification channel: ${e.message}")
            }
        }
    }

    private fun initFcmSubscriptions() {
        try {
            val fcm = FirebaseMessaging.getInstance()
            // Fetch token to guarantee device registration with Firebase Gateway
            fcm.token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM Device Token obtained: $token")
                } else {
                    Log.w(TAG, "FCM Device Token fetch failed: ${task.exception?.message}")
                }
                // Subscribe to all topics including all_users
                MyFirebaseMessagingService.subscribeAllTopics(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FCM subscriptions: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "SewayojanApp"
        const val NOTIFICATION_CHANNEL_ID = "sewayojan_job_notifications"
    }
}
