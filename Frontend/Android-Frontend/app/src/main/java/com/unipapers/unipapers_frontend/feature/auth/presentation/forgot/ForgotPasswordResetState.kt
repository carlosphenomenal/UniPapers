package com.unipapers.unipapers_frontend.feature.auth.presentation.forgot

data class ForgotPasswordResetState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

