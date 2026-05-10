package com.unipapers.unipapers_frontend.feature.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.unipapers.unipapers_frontend.MainActivity
import com.unipapers.unipapers_frontend.R
import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.feature.notifications.domain.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.graphics.createBitmap

@AndroidEntryPoint
class UniPapersFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var prefs: AppPreferences

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        const val ACTION_NEW_NOTIFICATION = "com.unipapers.unipapers_frontend.NEW_NOTIFICATION"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save the token locally
        prefs.saveFcmToken(token)
        // Send this token to the backend if there is an access token in the shared preferences
        sendTokenToBackend(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Notify the app that a new notification has been received
        val intent = Intent(ACTION_NEW_NOTIFICATION).apply {
            setPackage(packageName)
        }
        sendBroadcast(intent)

        // Show push notification
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "UniPapers",
                body = notification.body ?: ""
            )
        } ?: run {
            // Handle data message if notification is null
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            if (title != null && body != null) {
                showNotification(title, body)
            }
        }
    }

    private fun sendTokenToBackend(token: String) {
        val accessToken = prefs.getAccessToken()
        if (!accessToken.isNullOrBlank()) {
            scope.launch {
                repository.updateFcmToken(token)
            }
        }
        android.util.Log.d("FCM_TOKEN", "Device token: $token")
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "unipapers_notifications"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "UniPapers Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "UniPapers general notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}