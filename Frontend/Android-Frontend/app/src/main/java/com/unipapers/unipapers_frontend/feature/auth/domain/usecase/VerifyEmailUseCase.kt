package com.unipapers.unipapers_frontend.feature.auth.domain.usecase

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(verifyEmailRequestDto: VerifyEmailRequestDto): Resource<String> {
        return repository.verifyEmail(verifyEmailRequestDto)
    }
}
