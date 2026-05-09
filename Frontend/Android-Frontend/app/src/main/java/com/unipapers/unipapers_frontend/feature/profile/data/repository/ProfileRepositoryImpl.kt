package com.unipapers.unipapers_frontend.feature.profile.data.repository

import android.content.Context
import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.domain.model.User
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileRemoteDataSource
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto.UserDto
import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileResponse
import com.unipapers.unipapers_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ProfileRepository {

    override suspend fun getProfile(): Result<User> {
        return try {
            val remoteUser = remoteDataSource.getProfile()
            Result.success(remoteUser.toDomain())
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Failed to fetch profile", e)
            Result.failure(e) // ❗ DO NOT fallback yet
        }
    }

    override suspend fun updateProfile(year: Int, semester: Int): Result<Unit> {
        return runCatching {
            remoteDataSource.updateProfile(year, semester)
        }
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updatePassword(currentPassword, newPassword)
        }
    }

    // ✅ Mapping DTO → Domain
    // ✅ Mapping DTO → Domain with null safety
    private fun UserDto.toDomain() = User(
        id = id ?: "",
        fullName = fullName ?: "Unknown User",
        email = email ?: "",
        studentNumber = studentNumber ?: "N/A",
        programme = programme ?: "Not Assigned",
        yearOfStudy = yearOfStudy ?: 1,
        currentSemester = currentSemester ?: 1,
        freeViewsRemaining = freeViewsRemaining ?: 0,
        hasUnlockedAccess = hasUnlockedAccess ?: false,
        uploadCount = uploadCount ?: 0,
        downloadCount = downloadCount ?: 0
    )
}