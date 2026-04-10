package com.unipapers.unipapers_frontend.feature.profile.domain.usecase

import com.unipapers.unipapers_frontend.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = repository.updatePassword(currentPassword, newPassword)
}