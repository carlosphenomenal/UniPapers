package com.unipapers.feature.profile.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("yearOfStudy") val yearOfStudy: Int,
    @SerializedName("currentSemester") val currentSemester: Int
)