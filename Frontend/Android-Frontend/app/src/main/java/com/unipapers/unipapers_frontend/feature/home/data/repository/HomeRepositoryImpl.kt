package com.unipapers.unipapers_frontend.feature.home.data.repository

import android.util.Log
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.home.data.model.toPaper
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: UniPapersApiService,
    private val fileApi: FileApi
) : HomeRepository {
    override suspend fun getPastPapers(query: String?, filter: PaperType?): Result<List<Paper>> {
        return try {
            val response = apiService.getPastPapers(query, filter?.name)
            Result.success(response.map { it.toPaper() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSignedUrl(paperId: String): Result<String> {
        return try {
            val response = fileApi.getPresignedDownloadUrl(paperId)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Failed to get presigned download URL: ${response.message()}"))
            }

            val signedUrl = response.body()?.signedUrl

            if (signedUrl != null) {
                Result.success(signedUrl)
            } else {
                Result.failure(Exception("Signed URL is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
