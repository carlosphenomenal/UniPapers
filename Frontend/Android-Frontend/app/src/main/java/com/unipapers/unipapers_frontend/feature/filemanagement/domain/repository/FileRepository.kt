package com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository

import com.unipapers.unipapers_frontend.feature.filemanagement.data.model.FileUploadDto
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface FileRepository {

    val downloads: StateFlow<List<Download>>
    suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit>
    suspend fun downloadFile(pastPaperPublicId: String): Result<Unit>
    fun syncDownloadedFile(downloadId: Long)

}
