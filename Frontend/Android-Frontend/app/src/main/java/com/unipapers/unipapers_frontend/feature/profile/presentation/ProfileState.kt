package com.unipapers.unipapers_frontend.feature.profile.presentation

import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileResponse

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: ProfileResponse? = null,
    val error: String? = null,
    val isUpdatingProfile: Boolean = false,
    val isChangingPassword: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val passwordChangeError: String? = null,
    val showChangePasswordModal: Boolean = false,
    val showYearSemesterSheet: Boolean = false,
    val successMessage: String? = null
)