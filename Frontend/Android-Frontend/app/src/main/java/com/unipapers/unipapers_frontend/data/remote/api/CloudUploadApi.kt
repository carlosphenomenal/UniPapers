package com.unipapers.unipapers_frontend.data.remote.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Url

interface CloudUploadApi {

    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Header("Content-Type") contentType: String,
        @Body body: RequestBody
    ): Response<Unit>

}
