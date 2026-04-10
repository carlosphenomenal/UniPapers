package com.unipapers.unipapers_frontend.feature.profile.domain.usecase
class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getUserProfile()
    }
}