package com.unipapers.unipapers_frontend.feature.profile.domain.repository

import com.unipapers.unipapers_frontend.core.domain.model.User

interface ProfileRepository {
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(year: Int, semester: Int): Result<Unit>
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>
}