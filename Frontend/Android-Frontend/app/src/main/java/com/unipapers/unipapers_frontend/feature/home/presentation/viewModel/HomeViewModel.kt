package com.unipapers.unipapers_frontend.feature.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.DownloadFileUseCase
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.domain.usecase.GetPastPapersUseCase
import com.unipapers.unipapers_frontend.feature.home.domain.usecase.GetSignedUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPastPapersUseCase: GetPastPapersUseCase,
    private val getSignedUrlUseCase: GetSignedUrlUseCase,
    private val downloadFileUseCase: DownloadFileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    private var lastAction: (() -> Unit)? = null

    init {
        loadPastPapers()
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
}
