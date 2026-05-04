package com.unipapers.unipapers_frontend.core.data.remote.api

import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.ProgramResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface ProgramApiService {

    @GET("programs/get")
    suspend fun getAllPrograms(): Response<List<ProgramResponseDto>>
}


