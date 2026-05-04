package com.unipapers.unipapers_frontend.feature.auth.presentation.verify

data class VerifyState(
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val resendTimer: Int = 30,
    val canResend: Boolean = false,
    val email: String = "" // To show which email the code was sent to
)
