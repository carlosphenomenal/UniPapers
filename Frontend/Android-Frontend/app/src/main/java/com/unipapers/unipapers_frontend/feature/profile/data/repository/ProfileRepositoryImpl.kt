package com.unipapers.unipapers_frontend.feature.profile.data.repository

import android.content.Context
import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.domain.model.User
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileRemoteDataSource
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto.UserDto
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
            // ✅ 1. Try backend API first
            val remoteUser = remoteDataSource.getProfile()
            Result.success(remoteUser.toDomain())
        } catch (e: Exception) {

            // 🔁 2. Fallback to local JSON if backend fails
            try {
                val jsonString = context.assets.open("profile_mock.json")
                    .bufferedReader()
                    .use { it.readText() }

                val dto = gson.fromJson(jsonString, UserDto::class.java)

                Result.success(dto.toDomain())

            } catch (mockError: Exception) {
                // ❌ If both fail, return original error
                Result.failure(e)
            }
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
    private fun UserDto.toDomain() = User(
        id = id ?: email.orEmpty(),
        fullName = fullName.orEmpty(),
        email = email.orEmpty(),
        studentNumber = studentNumber.orEmpty(),
        programme = programme.orEmpty(),
        yearOfStudy = yearOfStudy ?: 1,
        currentSemester = semester ?: 1,
        freeViewsRemaining = 0,
        hasUnlockedAccess = false,
        uploadCount = uploadedPastPapersCount ?: 0,
        downloadCount = 0
    )
}