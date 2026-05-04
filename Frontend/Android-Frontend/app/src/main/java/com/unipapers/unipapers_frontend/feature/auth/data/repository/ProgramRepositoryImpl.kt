package com.unipapers.unipapers_frontend.feature.auth.data.repository

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.remote.api.ProgramApiService
import com.unipapers.unipapers_frontend.core.data.remote.dto.ErrorResponseDto
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.ProgramResponseDto
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.ProgramRepository
import retrofit2.Response
import javax.inject.Inject

class ProgramRepositoryImpl @Inject constructor(
    private val api: ProgramApiService,
    private val gson: Gson
) : ProgramRepository {

    override suspend fun getAllPrograms(): Resource<List<ProgramResponseDto>> {
        return handleResponse { api.getAllPrograms() }
    }

    private suspend fun <T> handleResponse(call: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Success but empty body")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = gson.fromJson(errorBody, ErrorResponseDto::class.java)
                Resource.Error(errorResponse?.message ?: "An unknown error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Couldn't reach server. Check your internet connection.")
        }
    }
}

