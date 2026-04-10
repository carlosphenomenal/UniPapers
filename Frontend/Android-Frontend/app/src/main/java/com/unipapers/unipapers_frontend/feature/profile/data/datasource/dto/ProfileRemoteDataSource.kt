package com.unipapers.feature.profile.data.datasource

import com.unipapers.feature.profile.data.datasource.dto.UpdatePasswordRequest
import com.unipapers.feature.profile.data.datasource.dto.UpdateProfileRequest
import com.unipapers.feature.profile.data.datasource.dto.UserDto
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val api: ProfileApiService
) {

    suspend fun getProfile(): UserDto = api.getProfile()

    suspend fun updateProfile(year: Int, semester: Int) =
        api.updateProfile(UpdateProfileRequest(year, semester))

    suspend fun updatePassword(current: String, new: String) =
        api.updatePassword(UpdatePasswordRequest(current, new))
}