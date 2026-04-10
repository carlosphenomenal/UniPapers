package com.unipapers.unipapers_frontend.feature.profile.presentation
data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)