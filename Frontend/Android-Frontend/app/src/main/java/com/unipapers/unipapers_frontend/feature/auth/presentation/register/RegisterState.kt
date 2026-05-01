package com.unipapers.unipapers_frontend.feature.auth.presentation.register

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val studentNumber: String = "",
    val programme: String = "",
    val yearOfStudy: Int = 1,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
