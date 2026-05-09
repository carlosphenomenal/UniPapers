package com.unipapers.unipapers_frontend.feature.notifications.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.remote.util.ErrorParser
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.core.data.remote.mapper.toDomain
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.NotificationRemoteDataSource
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto.SendNotificationRequestDto
import com.unipapers.unipapers_frontend.feature.notifications.domain.Notification
import com.unipapers.unipapers_frontend.feature.notifications.domain.NotificationType
import com.unipapers.unipapers_frontend.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource,
    private val gson: Gson
) : NotificationRepository {

    override suspend fun getNotifications(): Resource<List<Notification>> {
        return try {
            val response = remoteDataSource.getNotifications()
            if (response.isSuccessful) {
                val notifications = response.body()?.map { it.toDomain() } ?: emptyList()
                Resource.Success(notifications)
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun markAsRead(publicId: String): Resource<String> {
        return try {
            val response = remoteDataSource.markAsRead(publicId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun getUnreadCount(): Resource<Long> {
        return try {
            val response = remoteDataSource.getUnreadCount()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun sendNotification(
        userPublicId: String?,
        title: String,
        message: String,
        notificationType: NotificationType?
    ): Resource<String> {
        return try {
            val request = SendNotificationRequestDto(
                userPublicId = userPublicId,
                title = title,
                message = message,
                notificationType = notificationType?.name
            )
            val response = remoteDataSource.sendNotification(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun broadcastNotification(
        title: String,
        message: String,
        notificationType: NotificationType?
    ): Resource<String> {
        return try {
            val request = SendNotificationRequestDto(
                title = title,
                message = message,
                notificationType = notificationType?.name
            )
            val response = remoteDataSource.broadcastNotification(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }
}
