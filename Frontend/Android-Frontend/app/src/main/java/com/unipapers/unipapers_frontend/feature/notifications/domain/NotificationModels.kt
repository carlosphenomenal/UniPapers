package com.unipapers.unipapers_frontend.feature.notifications.domain

enum class NotificationType {
    GENERAL,
    UPLOAD_CONFIRMED,
    NEW_PAPER,
    PAPER_FLAGGED,
    FLAG_RESOLVED,
    WELCOME
}

data class Notification(
    val id: String,
    val title: String,
    val type: NotificationType,
    val message: String,
    val createdAt: String,
    val isRead: Boolean = false
)
