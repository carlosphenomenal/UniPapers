package com.unipapers.unipapers_frontend.feature.notifications.domain.usecase

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.notifications.domain.NotificationType
import com.unipapers.unipapers_frontend.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class BroadcastNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        title: String,
        message: String,
        notificationType: NotificationType? = null
    ): Resource<String> {
        return repository.broadcastNotification(title, message, notificationType)
    }
}
