package com.unipapers.unipapers_frontend.feature.notifications.presentation

import com.unipapers.unipapers_frontend.feature.notifications.domain.Notification

data class NotificationsState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val errorMessage: String? = null
)