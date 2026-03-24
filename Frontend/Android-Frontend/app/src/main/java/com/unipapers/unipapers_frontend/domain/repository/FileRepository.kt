package com.unipapers.unipapers_frontend.domain.repository

import com.unipapers.unipapers_frontend.data.remote.dto.FileUploadDto
import java.io.File

interface FileRepository {
    suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit>
}
