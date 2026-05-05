package com.unipapers.unipapers_frontend.feature.notifications.domain

enum class NotificationType {
    UPLOAD_CONFIRMED,
    NEW_PAPER,
    PAPER_FLAGGED,
    FLAG_RESOLVED,
    WELCOME
}

data class Notification( //shape of one notification item
    val id: String,
    val type: NotificationType,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean = false
)