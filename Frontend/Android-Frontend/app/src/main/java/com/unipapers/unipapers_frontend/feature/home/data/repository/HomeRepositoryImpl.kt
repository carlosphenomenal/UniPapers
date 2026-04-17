package com.unipapers.unipapers_frontend.feature.home.data.repository

import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.home.data.model.toPaper
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: UniPapersApiService
) : HomeRepository {
    override suspend fun getPastPapers(query: String?, filter: PaperType?): Result<List<Paper>> {
        return try {
            val response = apiService.getPastPapers(query, filter?.name)
            Result.success(response.map { it.toPaper() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
