package com.unipapers.unipapers_frontend.feature.upload.domain.repository

import com.unipapers.unipapers_frontend.feature.upload.domain.model.DownloadTrack
import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadDto
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FileRepository {
    suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit>
    suspend fun downloadFile(pastPaperPublicId: String): Result<Unit>
    fun syncDownloadedFile(downloadId: Long)

}
