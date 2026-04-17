package com.unipapers.unipapers_frontend.feature.home.data.datasource

import com.unipapers.unipapers_frontend.feature.home.data.model.PaperDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UniPapersApiService {
    @GET("past-papers/get")
    suspend fun getPastPapers(
        @Query("query") query: String?,
        @Query("filter") filter: String?
    ): List<PaperDto>
}