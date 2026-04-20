package com.unipapers.unipapers_frontend.feature.filemanagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.CancelDownloadUseCase
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.ObserveDownloadsUseCase
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.PauseDownloadUseCase
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.ResumeDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val observeDownloadsUseCase: ObserveDownloadsUseCase,
    private val pauseDownloadUseCase: PauseDownloadUseCase,
    private val resumeDownloadUseCase: ResumeDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase
) : ViewModel() {

    val downloads: StateFlow<List<Download>> = observeDownloadsUseCase()

    fun pauseDownload(downloadId: Long) {
        viewModelScope.launch {
            pauseDownloadUseCase(downloadId)
        }
    }

    fun resumeDownload(downloadId: Long) {
        viewModelScope.launch {
            resumeDownloadUseCase(downloadId)
        }
    }

    fun cancelDownload(downloadId: Long) {
        viewModelScope.launch {
            cancelDownloadUseCase(downloadId)
        }
    }
}
