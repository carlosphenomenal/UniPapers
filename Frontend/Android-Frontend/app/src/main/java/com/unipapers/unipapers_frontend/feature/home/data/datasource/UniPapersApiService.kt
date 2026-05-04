package com.unipapers.unipapers_frontend.feature.home.data.datasource

import com.unipapers.unipapers_frontend.core.data.remote.dto.CourseResponseDto
import com.unipapers.unipapers_frontend.feature.home.data.model.PaperDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UniPapersApiService {
    @GET("courses/get")
    suspend fun getCourses(): Response<List<CourseResponseDto>>

    @GET("past-papers/get")
    suspend fun getPastPapers(
        @Query("query") query: String?,
        @Query("filter") filter: String?
    ): Response<List<PaperDto>>
}
