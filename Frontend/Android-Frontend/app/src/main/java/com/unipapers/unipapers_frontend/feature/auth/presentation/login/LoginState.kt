package com.unipapers.unipapers_frontend.feature.auth.presentation.login

data class LoginState(
    val identifier: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
