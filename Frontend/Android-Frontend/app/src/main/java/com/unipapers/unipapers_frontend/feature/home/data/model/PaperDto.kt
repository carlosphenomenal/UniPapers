package com.unipapers.unipapers_frontend.feature.home.data.model

import com.google.gson.annotations.SerializedName
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType

data class PaperDto(
    @SerializedName("id") val id: String,
    @SerializedName("academicYear") val academicYear: String,
    @SerializedName("courseCode") val courseCode: String,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("type") val type: String
)

fun PaperDto.toPaper(): Paper {
    return Paper(
        id = id,
        academicYear = academicYear,
        courseCode = courseCode,
        courseName = courseName,
        type = try {
            PaperType.valueOf(type.uppercase())
        } catch (_: Exception) {
            PaperType.NOTES
        }
    )
}
