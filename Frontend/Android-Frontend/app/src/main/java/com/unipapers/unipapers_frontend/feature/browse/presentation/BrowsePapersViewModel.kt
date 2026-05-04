package com.unipapers.unipapers_frontend.feature.browse.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.unipapers.unipapers_frontend.core.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.browse.domain.usecase.GetCourseNameUseCase
import com.unipapers.unipapers_frontend.feature.browse.domain.usecase.GetPaperSignedUrlUseCase
import com.unipapers.unipapers_frontend.feature.browse.domain.usecase.GetPapersByCourseUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowsePapersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCourseNameUseCase: GetCourseNameUseCase,
    private val getPapersByCourseUnitUseCase: GetPapersByCourseUnitUseCase,
    private val getPaperSignedUrlUseCase: GetPaperSignedUrlUseCase
) : ViewModel() {

    private val courseCode: String = checkNotNull(savedStateHandle["courseCode"])

    private val _state = MutableStateFlow(BrowsePapersState())
    val state: StateFlow<BrowsePapersState> = _state.asStateFlow()

    init {
        loadPapers()
    }

    private fun loadPapers() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    courseCode = courseCode,
                    isLoading = true,
                    error = null
                )
            }

            val courseName = getCourseNameUseCase(courseCode).getOrNull().orEmpty().ifBlank { courseCode }

            getPapersByCourseUnitUseCase(courseCode)
                .onSuccess { papers ->
                    _state.update {
                        it.copy(
                            courseName = courseName,
                            paperCount = papers.size,
                            papers = papers,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            courseName = courseName,
                            paperCount = 0,
                            papers = emptyList(),
                            isLoading = false,
                            error = error.message ?: "Failed to load papers"
                        )
                    }
                }
        }
    }

    fun onTypeSelected(type: PaperType) {
        _state.update { it.copy(selectedType = type) }
    }

    fun onPaperClicked(paperId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isOpeningPdf = true, pendingPdfUrl = null, error = null)
            }

            getPaperSignedUrlUseCase(paperId)
                .onSuccess { signedUrl ->
                    _state.update {
                        it.copy(
                            isOpeningPdf = false,
                            pendingPdfUrl = signedUrl,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isOpeningPdf = false,
                            pendingPdfUrl = null,
                            error = error.message ?: "Failed to open paper"
                        )
                    }
                }
        }
    }

    fun consumePendingPdfUrl() {
        _state.update { it.copy(pendingPdfUrl = null) }
    }
}
