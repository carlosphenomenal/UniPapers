package com.unipapers.unipapers_frontend.feature.auth.domain.usecase

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResendCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<String> {
        return repository.resendVerificationCode(email)
    }
}
