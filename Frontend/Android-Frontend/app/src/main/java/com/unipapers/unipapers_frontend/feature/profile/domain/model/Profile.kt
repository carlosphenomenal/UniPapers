package com.unipapers.unipapers_frontend.feature.profile.domain.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val user: UserInfo,
    val stats: ProfileStats,
    @SerializedName("settings_options")
    val settingsOptions: List<SettingOption> = emptyList()
)

data class UserInfo(
    val fullName: String,
    val email: String,
    val institution: String,
    val studentId: String,
    val programme: String,
    val yearOfStudy: Int = 1,
    val currentSemester: Int = 1
)

data class ProfileStats(
    val uploadedPastPapers: Int
)

data class SettingOption(
    val id: String,
    val label: String,
)