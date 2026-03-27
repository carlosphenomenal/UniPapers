package com.unipapers.unipapers_frontend.feature.upload.domain.repository

import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadDto
import java.io.File

interface FileRepository {
    suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit>
}
