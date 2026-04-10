package com.unipapers.unipapers_frontend.feature.profile.data.repository
class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val response = remoteDataSource.getUserProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}