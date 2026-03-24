package com.unipapers.unipapers_frontend.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FileUploadDto(
    @SerializedName("coursePublicId") val coursePublicId: String,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("fileName") val fileName: String,
    @SerializedName("type") val type: String,
    @SerializedName("academicYear") val academicYear: String,
    @SerializedName("yearOfStudy") val yearOfStudy: Int,
    @SerializedName("semester") val semester: Int,
    @SerializedName("topicsNames") val topicsNames: List<String>
)
