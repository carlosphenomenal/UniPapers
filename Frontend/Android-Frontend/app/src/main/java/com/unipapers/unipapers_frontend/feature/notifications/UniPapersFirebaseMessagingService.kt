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

class UniPapersFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send this token to the backend
        // This is called when Firebase generates a new token for this device
        // Carlos needs this token to send push notifications to this specific phone
        sendTokenToBackend(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Called when a push notification arrives
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "UniPapers",
                body = notification.body ?: ""
            )
        }
    }

    private fun sendTokenToBackend(token: String) {
        // TODO: Call the backend API to save this token
        // This will be implemented when Carlos provides the endpoint
        android.util.Log.d("FCM_TOKEN", "Device token: $token")
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "unipapers_notifications"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "UniPapers Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}