package com.unipapers.unipapers_frontend.feature.notifications.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class NotificationResponseDto(
    @SerializedName("publicId")
    val publicId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("notificationType")
    val notificationType: String,
    @SerializedName("read")
    val read: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)
