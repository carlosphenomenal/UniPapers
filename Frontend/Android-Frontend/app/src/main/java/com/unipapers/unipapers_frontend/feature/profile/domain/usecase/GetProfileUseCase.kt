package com.unipapers.unipapers_frontend.feature.profile.domain.usecase

import com.unipapers.unipapers_frontend.core.domain.model.User
import com.unipapers.unipapers_frontend.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<User> = repository.getProfile()
}