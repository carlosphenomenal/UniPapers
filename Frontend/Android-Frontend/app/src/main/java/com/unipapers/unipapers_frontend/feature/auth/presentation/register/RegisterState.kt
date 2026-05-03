package com.unipapers.unipapers_frontend.feature.auth.presentation.register

import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.ProgramResponseDto

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val studentNumber: String = "",
    val programme: String = "",
    val programmeId: String = "", // This stores the public ID of the selected program
    val yearOfStudy: Int = 1,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isProgramsLoading: Boolean = false,
    val programs: List<ProgramResponseDto> = emptyList(),
    val error: String? = null,
    val programsError: String? = null
)
