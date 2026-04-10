package com.unipapers.unipapers_frontend.feature.profile.presentation

import com.unipapers.unipapers_frontend.core.domain.model.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isUpdatingProfile: Boolean = false,
    val isChangingPassword: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val passwordChangeError: String? = null,
    val showChangePasswordModal: Boolean = false,
    val showYearSemesterSheet: Boolean = false,
    val successMessage: String? = null
)