package com.unipapers.unipapers_frontend.feature.home.domain.usecase

import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetSignedUrlUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(paperId: String): Result<String> {
        return repository.getSignedUrl(paperId)
    }
}
