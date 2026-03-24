package com.unipapers.unipapers_frontend.domain.usecase

import com.unipapers.unipapers_frontend.data.remote.dto.FileUploadDto
import com.unipapers.unipapers_frontend.domain.repository.FileRepository
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(fileUploadDto: FileUploadDto, file: File): Result<Unit> {
        return repository.uploadFile(fileUploadDto, file)
    }
}