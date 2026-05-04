package com.unipapers.unipapers_frontend.feature.profile.data.datasource

import com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto.UpdatePasswordRequest
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto.UpdateProfileRequest
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApiService {

    @GET("profile/get")
    suspend fun getProfile(): UserDto

    @PUT("users/me/profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequest)

    @PUT("users/me/password")
    suspend fun updatePassword(@Body body: UpdatePasswordRequest)
}