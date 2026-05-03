package com.unipapers.unipapers_frontend.feature.auth.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.data.remote.api.AuthApiService
import com.unipapers.unipapers_frontend.core.data.remote.dto.ErrorResponseDto
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginResponseDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SignupRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val prefs: AppPreferences,
    private val gson: Gson
) : AuthRepository {

    override suspend fun signup(signupRequestDto: SignupRequestDto): Resource<String> {
        return handleResponse { api.signup(signupRequestDto) }
    }

    override suspend fun verifyEmail(verifyEmailRequestDto: VerifyEmailRequestDto): Resource<String> {
        return handleResponse { api.verifyEmail(verifyEmailRequestDto) }
    }

    override suspend fun resendVerificationCode(email: String): Resource<String> {
        return handleResponse { api.resendVerificationCode(email) }
    }

    override suspend fun login(loginRequestDto: LoginRequestDto): Resource<LoginResponseDto> {
        return try {
            val response = api.login(loginRequestDto)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    prefs.saveTokens(loginResponse.accessToken, loginResponse.refreshToken)
                    prefs.saveUser(loginResponse.publicId, loginResponse.email)
                    Resource.Success(loginResponse)
                } ?: Resource.Error("Empty response body")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = gson.fromJson(errorBody, ErrorResponseDto::class.java)
                Resource.Error(errorResponse?.message ?: "An unknown error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Couldn't reach server. Check your internet connection.")
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
        } catch (e: Exception) {
            Resource.Error("Network error")
        }
    }

    private suspend fun <T> handleResponse(call: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Success but empty body")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = gson.fromJson(errorBody, ErrorResponseDto::class.java)
                Resource.Error(errorResponse?.message ?: "An unknown error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Couldn't reach server. Check your internet connection.")
        }
    }
}
