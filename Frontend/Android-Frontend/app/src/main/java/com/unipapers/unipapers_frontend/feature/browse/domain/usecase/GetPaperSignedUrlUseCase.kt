package com.unipapers.unipapers_frontend.feature.browse.domain.usecase

import com.unipapers.unipapers_frontend.feature.browse.domain.repository.BrowseRepository
import javax.inject.Inject

class GetPaperSignedUrlUseCase @Inject constructor(
    private val repository: BrowseRepository
) {
    suspend operator fun invoke(paperId: String): Result<String> {
        return repository.getPaperSignedUrl(paperId)
    }
}

