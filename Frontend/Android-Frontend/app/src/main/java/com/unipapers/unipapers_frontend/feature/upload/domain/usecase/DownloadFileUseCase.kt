package com.unipapers.unipapers_frontend.feature.upload.domain.usecase

import com.unipapers.unipapers_frontend.feature.upload.domain.repository.FileRepository
import jakarta.inject.Inject

class DownloadFileUseCase @Inject constructor(
    private val repository: FileRepository
) {
     suspend operator fun invoke(pastPaperPublicId: String): Result<Unit> {
         return repository.downloadFile(pastPaperPublicId)
     }
}