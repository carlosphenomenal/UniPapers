package com.unipapers.unipapers_frontend.core.data.remote.api

import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginResponseDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SignupRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    @POST("auth/signup")
    suspend fun signup(
        @Body signupRequestDto: SignupRequestDto
    ): Response<String>

    @POST("auth/verify-email")
    suspend fun verifyEmail(
        @Body verifyEmailRequestDto: VerifyEmailRequestDto
    ): Response<String>

    @POST("auth/resend-verification-code")
    suspend fun resendVerificationCode(
        @Query("email") email: String
    ): Response<String>

    @POST("auth/login")
    suspend fun login(
        @Body loginRequestDto: LoginRequestDto
    ): Response<LoginResponseDto>

    @GET("auth/me")
    suspend fun me(
        @Header("Authorization") token: String
    ): Response<Map<String, String>>

    @POST("auth/refresh")
    suspend fun refresh(
        @Query("accessToken") accessToken: String
    ): Response<LoginResponseDto>

    @POST("auth/logout")
    suspend fun logout(
        @Query("refreshToken") refreshToken: String
    ): Response<Unit>
}
