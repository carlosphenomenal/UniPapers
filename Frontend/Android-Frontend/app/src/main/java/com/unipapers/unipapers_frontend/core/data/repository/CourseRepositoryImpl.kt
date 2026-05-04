package com.unipapers.unipapers_frontend.core.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.remote.dto.toCourse
import com.unipapers.unipapers_frontend.core.data.remote.util.ErrorParser
import com.unipapers.unipapers_frontend.core.domain.model.Course
import com.unipapers.unipapers_frontend.core.domain.repository.CourseRepository
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val apiService: UniPapersApiService,
    private val gson: Gson
) : CourseRepository {
    override suspend fun getCourses(): Result<List<Course>> {
        return try {
            val response = apiService.getCourses()
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toCourse() } ?: emptyList())
            } else {
                val errorMessage = ErrorParser.parseErrorMessage(response, gson)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

