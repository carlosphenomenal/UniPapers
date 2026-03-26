package com.unipapers.unipapers_frontend.feature.upload.data.datasource

import com.google.gson.annotations.SerializedName

data class FileUploadResponseDto(
    @SerializedName("publicId") val publicId: String,
    @SerializedName("signedUrl") val signedUrl: String
)
