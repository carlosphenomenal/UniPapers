package com.unipapers.unipapers_frontend.feature.notifications.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.unipapers.unipapers_frontend.feature.notifications.domain.Notification
import com.unipapers.unipapers_frontend.feature.notifications.domain.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// JSON RESPONSE SHAPE
// ─────────────────────────────────────────────

data class NotificationJson(
    val id: String,
    val type: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean
)

data class NotificationsDataJson(
    @SerializedName("notifications") val notifications: List<NotificationJson>
)

// ─────────────────────────────────────────────
// VIEWMODEL
// ─────────────────────────────────────────────

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val jsonString = getApplication<Application>()
                    .assets
                    .open("notifications_data.json")
                    .bufferedReader()
                    .use { it.readText() }

                val data = Gson().fromJson(jsonString, NotificationsDataJson::class.java)

                val notifications = data.notifications.map { it.toNotification() }

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        notifications = notifications
                    )
                }

            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun markAllRead() {
        _state.update { currentState ->
            currentState.copy(
                notifications = currentState.notifications.map {
                    it.copy(isRead = true)
                }
            )
        }
    }
}

// ─────────────────────────────────────────────
// EXTENSION FUNCTION
// ─────────────────────────────────────────────

fun NotificationJson.toNotification(): Notification {
    return Notification(
        id = this.id,
        type = when (this.type.trim().uppercase()) {
            "UPLOAD_CONFIRMED" -> NotificationType.UPLOAD_CONFIRMED
            "NEW_PAPER" -> NotificationType.NEW_PAPER
            "PAPER_FLAGGED" -> NotificationType.PAPER_FLAGGED
            "FLAG_RESOLVED" -> NotificationType.FLAG_RESOLVED
            else -> NotificationType.WELCOME
        },
        message = this.message,
        timeAgo = this.timeAgo,
        isRead = this.isRead
    )
}