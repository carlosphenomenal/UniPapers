package com.unipapers.unipapers_frontend.feature.notifications.presentation

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.notifications.UniPapersFirebaseMessagingService
import com.unipapers.unipapers_frontend.feature.notifications.domain.usecase.GetNotificationsUseCase
import com.unipapers.unipapers_frontend.feature.notifications.domain.usecase.MarkNotificationsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    application: Application,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationsReadUseCase: MarkNotificationsReadUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == UniPapersFirebaseMessagingService.ACTION_NEW_NOTIFICATION) {
                loadNotifications()
            }
        }
    }

    init {
        loadNotifications()
        registerReceiver()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getNotificationsUseCase()) {
                is Resource.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        notifications = result.data ?: emptyList(),
                        errorMessage = null
                    ) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    ) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun markAsRead(publicId: String) {
        viewModelScope.launch {
            when (val result = markNotificationsReadUseCase(publicId)) {
                is Resource.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.map {
                                if (it.id == publicId) it.copy(isRead = true) else it
                            }
                        )
                    }
                    // Notify other components (like Home) to refresh unread count
                    val intent = Intent(UniPapersFirebaseMessagingService.ACTION_NEW_NOTIFICATION).apply {
                        setPackage(getApplication<Application>().packageName)
                    }
                    getApplication<Application>().sendBroadcast(intent)
                }
                is Resource.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            val unreadNotifications = _state.value.notifications.filter { !it.isRead }
            if (unreadNotifications.isEmpty()) return@launch

            var anySuccess = false
            for (notification in unreadNotifications) {
                when (val result = markNotificationsReadUseCase(notification.id)) {
                    is Resource.Success -> {
                        anySuccess = true
                        _state.update { currentState ->
                            currentState.copy(
                                notifications = currentState.notifications.map {
                                    if (it.id == notification.id) it.copy(isRead = true) else it
                                }
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                    else -> {}
                }
            }
            
            if (anySuccess) {
                // Notify other components (like Home) to refresh unread count
                val intent = Intent(UniPapersFirebaseMessagingService.ACTION_NEW_NOTIFICATION).apply {
                    setPackage(getApplication<Application>().packageName)
                }
                getApplication<Application>().sendBroadcast(intent)
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter(UniPapersFirebaseMessagingService.ACTION_NEW_NOTIFICATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getApplication<Application>().registerReceiver(
                notificationReceiver, 
                filter, 
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                getApplication(),
                notificationReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(notificationReceiver)
        } catch (_: Exception) {
            // Receiver might not be registered
        }
    }
}
