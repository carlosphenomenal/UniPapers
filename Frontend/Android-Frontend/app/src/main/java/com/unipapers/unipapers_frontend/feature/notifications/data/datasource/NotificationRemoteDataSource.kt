package com.unipapers.unipapers_frontend.feature.notifications.data.datasource

import com.unipapers.unipapers_frontend.core.data.remote.dto.MessageResponseDto
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto.NotificationResponseDto
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto.SendNotificationRequestDto
import retrofit2.Response
import javax.inject.Inject

class NotificationRemoteDataSource @Inject constructor(
    private val apiService: NotificationApiService
) {
    suspend fun getNotifications(): Response<List<NotificationResponseDto>> {
        return apiService.getNotifications()
    }

    suspend fun markAsRead(publicId: String): Response<MessageResponseDto> {
        return apiService.markAsRead(publicId)
    }

    suspend fun getUnreadCount(): Response<Long> {
        return apiService.getUnreadCount()
    }

    suspend fun sendNotification(request: SendNotificationRequestDto): Response<MessageResponseDto> {
        return apiService.sendNotification(request)
    }

    suspend fun broadcastNotification(request: SendNotificationRequestDto): Response<MessageResponseDto> {
        return apiService.broadcastNotification(request)
    }
}
