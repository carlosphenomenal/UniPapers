package com.unipapers.feature.profile.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class UpdatePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)