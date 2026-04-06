package com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository

import com.unipapers.unipapers_frontend.feature.filemanagement.data.model.FileUploadDto
import java.io.File

interface FileRepository {
    suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit>
    suspend fun downloadFile(pastPaperPublicId: String): Result<Unit>
    fun syncDownloadedFile(downloadId: Long)

}
