package com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase

import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import javax.inject.Inject

class CancelDownloadUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(downloadId: Long): Result<Unit> {
        return repository.cancelDownload(downloadId)
    }
}
