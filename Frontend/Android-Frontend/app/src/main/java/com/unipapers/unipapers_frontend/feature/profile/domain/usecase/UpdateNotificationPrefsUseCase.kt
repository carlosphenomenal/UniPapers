package com.unipapers.unipapers_frontend.feature.profile.domain.usecase

import com.unipapers.unipapers_frontend.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateNotificationPrefsUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        year: Int,
        semester: Int
    ): Result<Unit> = repository.updateProfile(year, semester)
}