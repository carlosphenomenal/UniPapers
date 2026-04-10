package com.unipapers.unipapers_frontend.feature.profile.domain.repository
interface ProfileRepository {
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(year: Int, semester: Int): Result<Unit>
    suspend fun updatePassword(current: String, new: String): Result<Unit>
}