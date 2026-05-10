package com.unipapers.unipapers_frontend.feature.notifications.data.datasource

import com.unipapers.unipapers_frontend.core.data.remote.dto.MessageResponseDto
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto.NotificationResponseDto
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto.SendNotificationRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @GET("notifications/get")
    suspend fun getNotifications(): Response<List<NotificationResponseDto>>

    @PUT("notifications/mark-as-read/{publicId}")
    suspend fun markAsRead(
        @Path("publicId") publicId: String
    ): Response<MessageResponseDto>

    @DELETE("notifications/delete/{publicId}")
    suspend fun deleteNotification(
        @Path("publicId") publicId: String
    ): Response<MessageResponseDto>

    @GET("notifications/count-unread")
    suspend fun getUnreadCount(): Response<Long>

    @POST("notifications/send")
    suspend fun sendNotification(
        @Body request: SendNotificationRequestDto
    ): Response<MessageResponseDto>

    @POST("notifications/broadcast")
    suspend fun broadcastNotification(
        @Body request: SendNotificationRequestDto
    ): Response<MessageResponseDto>

    @PUT("auth/update-fcm-token")
    suspend fun updateFcmToken(
        @Query("token") token: String
    ): Response<MessageResponseDto>
}
