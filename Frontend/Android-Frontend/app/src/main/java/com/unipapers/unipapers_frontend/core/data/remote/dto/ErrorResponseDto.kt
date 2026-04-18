package com.unipapers.unipapers_frontend.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ErrorResponseDto(
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("traceId")
    val traceId: String? = null
)
