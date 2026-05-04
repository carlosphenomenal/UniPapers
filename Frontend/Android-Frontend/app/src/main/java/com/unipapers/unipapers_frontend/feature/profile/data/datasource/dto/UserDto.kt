package com.unipapers.unipapers_frontend.feature.profile.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("studentNumber") val studentNumber: String? = null,
    @SerializedName("programme") val programme: String? = null,
    @SerializedName("yearOfStudy") val yearOfStudy: Int? = null,
    @SerializedName("semester") val semester: Int? = null,
    @SerializedName("uploadedPastPapersCount") val uploadedPastPapersCount: Int? = null
)