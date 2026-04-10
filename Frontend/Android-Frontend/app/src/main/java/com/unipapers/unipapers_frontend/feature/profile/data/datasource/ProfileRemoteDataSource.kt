package com.unipapers.unipapers_frontend.feature.profile.data.datasource
interface ProfileApiService {
    @GET("users/me")
    suspend fun getUserProfile(): User [cite: 158]

    @PUT("users/me/profile")
    suspend fun updateProfile(@Body updateRequest: ProfileUpdateRequest): Response<Unit> [cite: 158]
}