package com.unipapers.unipapers_frontend.feature.upload.data.datasource

import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadDto
import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FileApi {

    @POST("files/init-upload")
    suspend fun initializeUploadFile(@Body fileUploadDto: FileUploadDto): Response<FileUploadResponseDto>

    @PUT("files/confirm-upload/{pastPaperPublicId}")
    suspend fun confirmUpload(@Path("pastPaperPublicId") pastPaperPublicId: String): Response<Unit>

}
