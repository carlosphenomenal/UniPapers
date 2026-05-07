package com.unipapers.unipapers_frontend.feature.auth.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.data.remote.api.AuthApiService
import com.unipapers.unipapers_frontend.core.data.remote.util.ErrorParser
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginResponseDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SendCodeRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SendCodeResponseDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SignupRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.UpdatePasswordRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val prefs: AppPreferences,
    private val gson: Gson
) : AuthRepository {

    override suspend fun signup(signupRequestDto: SignupRequestDto): Resource<String> {
        return try {
            val response = api.signup(signupRequestDto)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (_: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun verifyEmail(verifyEmailRequestDto: VerifyEmailRequestDto): Resource<String> {
        return try {
            val response = api.verifyEmail(verifyEmailRequestDto)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (_: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun resendVerificationCode(email: String): Resource<String> {
        return try {
            val response = api.resendVerificationCode(email)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (_: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun sendCode(identifier: String): Resource<SendCodeResponseDto> {
        return try {
            val response = api.sendCode(SendCodeRequestDto(identifier.trim()))
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (_: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun login(loginRequestDto: LoginRequestDto): Resource<LoginResponseDto> {
        return try {
            val response = api.login(loginRequestDto)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    prefs.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                    prefs.saveUser(
                        userId = loginResponse.publicId,
                        email = loginResponse.email,
                        deviceId = loginResponse.deviceId
                    )
                    Resource.Success(loginResponse)
                } ?: Resource.Error("Empty response body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (_: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun updatePassword(updatePasswordRequestDto: UpdatePasswordRequestDto): Resource<String> {
        return try {
            val response = api.updatePassword(updatePasswordRequestDto)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it.message) }
                    ?: Resource.Error("Success but empty body")
            } else {
                val message = ErrorParser.parseErrorMessage(response, gson)
                Resource.Error(message)
            }
        } catch (_: Exception) {
            Resource.Error("Couldn't reach server. Check your internet connection.")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            val refreshToken = prefs.getRefreshToken()
            if (refreshToken != null) {
                api.logout(refreshToken)
            }
            prefs.clearTokens()
            Resource.Success(Unit)
        } catch (_: Exception) {
            Resource.Success(Unit) // Still clear tokens locally
        }
    }

    override suspend fun authenticate(): Resource<Unit> {
        return try {
            val token = prefs.getAccessToken() ?: return Resource.Error("No token found")
            val response = api.me("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Unauthorized")
            }
        } catch (_: Exception) {
            Resource.Error("Network error")
        }
    }

}
