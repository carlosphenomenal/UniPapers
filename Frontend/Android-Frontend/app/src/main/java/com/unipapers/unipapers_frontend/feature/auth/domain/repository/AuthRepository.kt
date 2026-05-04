package com.unipapers.unipapers_frontend.feature.auth.domain.repository

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginResponseDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SendCodeResponseDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SignupRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.UpdatePasswordRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto

interface AuthRepository {
    suspend fun signup(signupRequestDto: SignupRequestDto): Resource<String>
    suspend fun verifyEmail(verifyEmailRequestDto: VerifyEmailRequestDto): Resource<String>
    suspend fun resendVerificationCode(email: String): Resource<String>
    suspend fun sendCode(identifier: String): Resource<SendCodeResponseDto>
    suspend fun login(loginRequestDto: LoginRequestDto): Resource<LoginResponseDto>
    suspend fun updatePassword(updatePasswordRequestDto: UpdatePasswordRequestDto): Resource<String>
    suspend fun logout(): Resource<Unit>
    suspend fun authenticate(): Resource<Unit>
}
