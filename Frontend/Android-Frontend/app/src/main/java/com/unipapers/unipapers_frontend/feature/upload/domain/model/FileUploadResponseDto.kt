package com.unipapers.unipapers_frontend.feature.upload.domain.model

import com.google.gson.annotations.SerializedName

data class FileUploadResponseDto(
    @SerializedName("publicId") val publicId: String,
    @SerializedName("signedUrl") val signedUrl: String
)