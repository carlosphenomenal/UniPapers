package com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("studentNumber") val studentNumber: String?,
    @SerializedName("programme") val programme: String?,
    @SerializedName("yearOfStudy") val yearOfStudy: Int?,
    @SerializedName("currentSemester") val currentSemester: Int?,
    @SerializedName("freeViewsRemaining") val freeViewsRemaining: Int?,
    @SerializedName("hasUnlockedAccess") val hasUnlockedAccess: Boolean?,
    @SerializedName("uploadCount") val uploadCount: Int?,
    @SerializedName("downloadCount") val downloadCount: Int?
)