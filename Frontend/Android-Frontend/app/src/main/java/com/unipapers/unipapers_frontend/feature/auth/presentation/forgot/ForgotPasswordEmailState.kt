package com.unipapers.unipapers_frontend.feature.auth.presentation.forgot

data class ForgotPasswordEmailState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
