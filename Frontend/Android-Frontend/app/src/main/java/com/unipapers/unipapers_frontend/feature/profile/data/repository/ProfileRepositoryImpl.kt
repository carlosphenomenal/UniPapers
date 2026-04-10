package com.unipapers.unipapers_frontend.feature.profile.data.repository

import com.unipapers.unipapers_frontend.core.domain.model.User
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileRemoteDataSource
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto.UserDto
import com.unipapers.unipapers_frontend.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {

    override suspend fun getProfile(): Result<User> = runCatching {
        remoteDataSource.getProfile().toDomain()
    }

    override suspend fun updateProfile(year: Int, semester: Int): Result<Unit> = runCatching {
        remoteDataSource.updateProfile(year, semester)
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        remoteDataSource.updatePassword(currentPassword, newPassword)
    }

    private fun UserDto.toDomain() = User(
        id = id,
        fullName = fullName,
        email = email,
        studentNumber = studentNumber,
        programme = programme,
        yearOfStudy = yearOfStudy,
        currentSemester = currentSemester,
        freeViewsRemaining = freeViewsRemaining,
        hasUnlockedAccess = hasUnlockedAccess,
        uploadCount = uploadCount,
        downloadCount = downloadCount
    )
}