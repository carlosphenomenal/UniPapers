package com.unipapers.unipapers_frontend.feature.home.presentation.viewModel

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
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.DownloadFileUseCase
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.domain.usecase.GetPastPapersUseCase
import com.unipapers.unipapers_frontend.feature.home.domain.usecase.GetSignedUrlUseCase
import com.unipapers.unipapers_frontend.feature.notifications.UniPapersFirebaseMessagingService
import com.unipapers.unipapers_frontend.feature.notifications.domain.usecase.GetUnreadCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getPastPapersUseCase: GetPastPapersUseCase,
    private val getSignedUrlUseCase: GetSignedUrlUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    private var lastAction: (() -> Unit)? = null

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == UniPapersFirebaseMessagingService.ACTION_NEW_NOTIFICATION) {
                loadUnreadCount()
            }
        }
    }

    init {
        loadPastPapers()
        loadUnreadCount()
        registerReceiver()
    }

    private fun loadPastPapers() {
        lastAction = { loadPastPapers() }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getPastPapersUseCase(
                query = _state.value.searchQuery,
                filter = _state.value.selectedFilter
            )
            result.onSuccess { papers ->
                _state.update { it.copy(
                    isLoading = false,
                    papers = papers,
                    errorMessage = null
                ) }
            }.onFailure { error ->
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "An unknown error occurred"
                ) }
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            when (val result = getUnreadCountUseCase()) {
                is Resource.Success -> {
                    _state.update { it.copy(unreadNotificationsCount = result.data ?: 0L) }
                }
                else -> {
                    // Fail silently for unread count
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        loadPastPapers()
    }

    fun onFilterSelected(filter: PaperType?) {
        _state.update { it.copy(selectedFilter = filter) }
        loadPastPapers()
    }

    fun onPaperClicked(paperId: String) {
        lastAction = { onPaperClicked(paperId) }
        viewModelScope.launch {
            _state.update { it.copy(isOpeningPdf = true, errorMessage = null) }
            val result = getSignedUrlUseCase(paperId)
            result.onSuccess { url ->
                _state.update { it.copy(
                    isOpeningPdf = false,
                    signedUrl = url
                ) }
            }.onFailure { error ->
                _state.update { it.copy(
                    isOpeningPdf = false,
                    errorMessage = error.message ?: "Failed to get signed URL"
                ) }
            }
        }
    }

    fun onDownloadClicked(paperId: String) {
        viewModelScope.launch {
            downloadFileUseCase(paperId).onFailure { error ->
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    fun retry() {
        lastAction?.invoke()
    }

    fun resetSignedUrl() {
        _state.update { it.copy(signedUrl = null) }
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
        } catch (e: Exception) {
            // Receiver might not be registered
        }
    }
}
