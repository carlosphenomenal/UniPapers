package com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase

import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveDownloadsUseCase @Inject constructor(
    private val repository: FileRepository
) {
    operator fun invoke(): StateFlow<List<Download>> {
        return repository.downloads
    }
}
