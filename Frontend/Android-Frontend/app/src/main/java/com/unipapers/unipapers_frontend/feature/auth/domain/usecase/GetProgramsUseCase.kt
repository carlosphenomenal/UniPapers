package com.unipapers.unipapers_frontend.feature.auth.domain.usecase

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.ProgramResponseDto
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.ProgramRepository
import javax.inject.Inject

class GetProgramsUseCase @Inject constructor(
    private val repository: ProgramRepository
) {
    suspend operator fun invoke(): Resource<List<ProgramResponseDto>> {
        return repository.getAllPrograms()
    }
}

