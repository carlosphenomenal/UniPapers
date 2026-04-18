package com.unipapers.unipapers_frontend.feature.home.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.remote.util.ErrorParser
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.home.data.model.toPaper
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: UniPapersApiService,
    private val fileApi: FileApi,
    private val gson: Gson
) : HomeRepository {
    override suspend fun getPastPapers(query: String?, filter: PaperType?): Result<List<Paper>> {
        return try {
            val response = apiService.getPastPapers(query, filter?.name)
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toPaper() } ?: emptyList())
            } else {
                val errorMessage = ErrorParser.parseErrorMessage(response, gson)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSignedUrl(paperId: String): Result<String> {
        return try {
            val response = fileApi.getPresignedDownloadUrl(paperId)
            if (!response.isSuccessful) {
                val errorMessage = ErrorParser.parseErrorMessage(response, gson)
                return Result.failure(Exception(errorMessage))
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
