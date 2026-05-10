package com.unipapers.unipapers_frontend.core.data.remote.mapper

import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto.NotificationResponseDto
import com.unipapers.unipapers_frontend.feature.notifications.domain.Notification
import com.unipapers.unipapers_frontend.feature.notifications.domain.NotificationType

fun NotificationResponseDto.toDomain(): Notification {
    return Notification(
        id = publicId,
        title = title,
        type = try {
            NotificationType.valueOf(notificationType)
        } catch (e: Exception) {
            NotificationType.GENERAL
        },
        message = message,
        createdAt = createdAt,
        isRead = read
    )
}
