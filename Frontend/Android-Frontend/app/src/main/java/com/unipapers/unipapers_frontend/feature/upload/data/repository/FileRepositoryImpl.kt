package com.unipapers.unipapers_frontend.feature.upload.data.repository

import com.unipapers.unipapers_frontend.feature.upload.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.upload.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.upload.data.datasource.FileUploadDto
import com.unipapers.unipapers_frontend.feature.upload.domain.repository.FileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi,
    private val cloudUploadApi: CloudUploadApi
) : FileRepository {

    override suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit> {
        return try {
            // Initialize upload
            val initResponse = fileApi.initializeUploadFile(fileUploadDto)
            if (!initResponse.isSuccessful) {
                return Result.failure(Exception("Failed to initialize upload: ${initResponse.message()}"))
            }

            val responseBody = initResponse.body()
                ?: return Result.failure(Exception("Response body is null"))

            val publicId = responseBody.publicId
            val signedUrl = responseBody.signedUrl

            // Upload file to signed URL
            val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val uploadResponse = cloudUploadApi.uploadFile(
                url = signedUrl,
                contentType = "application/pdf",
                body = requestBody
            )

            if (!uploadResponse.isSuccessful) {
                return Result.failure(Exception("Failed to upload file to bucket: ${uploadResponse.message()}"))
            }

            // confirm upload
            val confirmResponse = fileApi.confirmUpload(publicId)
            if (!confirmResponse.isSuccessful) {
                return Result.failure(Exception("Failed to confirm upload: ${confirmResponse.message()}"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
