package com.unipapers.unipapers_frontend.feature.upload.domain.model

import com.google.gson.annotations.SerializedName

data class FileDownloadResponseDto(
    @SerializedName("key") val key: String,
    @SerializedName("signedUrl") val signedUrl: String
)
