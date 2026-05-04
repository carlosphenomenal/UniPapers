package com.unipapers.unipapers_frontend.feature.auth.domain.repository

import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.ProgramResponseDto

interface ProgramRepository {
    suspend fun getAllPrograms(): Resource<List<ProgramResponseDto>>
}

