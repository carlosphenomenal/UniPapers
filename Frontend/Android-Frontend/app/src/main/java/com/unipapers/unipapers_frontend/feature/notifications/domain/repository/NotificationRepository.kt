package com.unipapers.unipapers_frontend.feature.notifications.domain.repository

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.notifications.domain.Notification
import com.unipapers.unipapers_frontend.feature.notifications.domain.NotificationType

interface NotificationRepository {
    suspend fun getNotifications(): Resource<List<Notification>>
    suspend fun markAsRead(publicId: String): Resource<String>
    suspend fun getUnreadCount(): Resource<Long>
    suspend fun sendNotification(
        userPublicId: String?,
        title: String,
        message: String,
        notificationType: NotificationType?
    ): Resource<String>
    suspend fun broadcastNotification(
        title: String,
        message: String,
        notificationType: NotificationType?
    ): Resource<String>
}
