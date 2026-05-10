package com.unipapers.unipapers_frontend.feature.notifications.domain.usecase

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(publicId: String): Resource<String> {
        return repository.deleteNotification(publicId)
    }
}
