package com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class SendNotificationRequestDto(
    @SerializedName("userPublicId")
    val userPublicId: String? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("notificationType")
    val notificationType: String? = null
)
