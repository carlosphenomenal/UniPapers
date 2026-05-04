package com.unipapers.unipapers_frontend.core.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.unipapers.unipapers_frontend.core.domain.model.Course

data class CourseResponseDto(
    @SerializedName("courseCode") val courseCode: String,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("publicId") val publicId: String? = null
)

fun CourseResponseDto.toCourse(): Course {
    return Course(
        courseCode = courseCode,
        courseName = courseName,
        publicId = publicId.orEmpty()
    )
}

