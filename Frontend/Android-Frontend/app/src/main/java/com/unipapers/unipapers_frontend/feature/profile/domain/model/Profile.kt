package com.unipapers.unipapers_frontend.feature.profile.domain.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val user: UserInfo,
    val stats: UserStats,
    @SerializedName("settings_options")
    val settingsOptions: List<SettingOption> = emptyList()
)

data class UserInfo(
    val fullName: String,
    val email: String,
    val institution: String,
    val studentId: String
)

data class UserStats(
    val uploadedPastPapers: Int
)

data class SettingOption(
    val id: String,
    val label: String,
)