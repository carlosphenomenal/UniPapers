package com.unipapers.unipapers_frontend.feature.browse.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.remote.util.ErrorParser
import com.unipapers.unipapers_frontend.core.domain.model.Paper
import com.unipapers.unipapers_frontend.core.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.browse.domain.repository.BrowseRepository
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.home.data.model.PaperDto
import javax.inject.Inject

class BrowseRepositoryImpl @Inject constructor(
    private val apiService: UniPapersApiService,
    private val fileApi: FileApi,
    private val gson: Gson
) : BrowseRepository {
    override suspend fun getPastPapers(courseCode: String, filter: PaperType?): Result<List<Paper>> {
        return try {
            val response = apiService.getPastPapers(courseCode, filter?.name)
            if (response.isSuccessful) {
                val papers = response.body()?.map { it.toCorePaper() } ?: emptyList()
                Result.success(papers)
            } else {
                val errorMessage = ErrorParser.parseErrorMessage(response, gson)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPaperSignedUrl(paperId: String): Result<String> {
        return try {
            val response = fileApi.getPresignedDownloadUrl(paperId)
            if (!response.isSuccessful) {
                val errorMessage = ErrorParser.parseErrorMessage(response, gson)
                return Result.failure(Exception(errorMessage))
            }

            val signedUrl = response.body()?.signedUrl
            if (signedUrl.isNullOrBlank()) {
                Result.failure(Exception("Signed URL is null"))
            } else {
                Result.success(signedUrl)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun PaperDto.toCorePaper(): Paper {
    val mappedType = runCatching { PaperType.valueOf(type.uppercase()) }
        .getOrDefault(PaperType.EXAM)
    return Paper(
        id = id,
        academicYear = academicYear,
        courseCode = courseCode,
        courseName = courseName,
        type = mappedType
    )
}

