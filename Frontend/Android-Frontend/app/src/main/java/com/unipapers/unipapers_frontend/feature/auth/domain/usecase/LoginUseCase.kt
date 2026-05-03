package com.unipapers.unipapers_frontend.feature.auth.domain.usecase

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginResponseDto
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(loginRequestDto: LoginRequestDto): Resource<LoginResponseDto> {
        if (loginRequestDto.identifier.isBlank() || loginRequestDto.password.isBlank()) {
            return Resource.Error("Identifier and password cannot be empty")
        }
        return repository.login(loginRequestDto)
    }
}
