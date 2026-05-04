package com.unipapers.unipapers_frontend.feature.auth.presentation.forgot

data class ForgotPasswordVerifyState(
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val resendTimer: Int = 30,
    val canResend: Boolean = false,
    val email: String = ""
)

