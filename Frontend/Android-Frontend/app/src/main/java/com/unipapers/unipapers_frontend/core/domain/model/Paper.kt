package com.unipapers.unipapers_frontend.core.domain.model

data class Paper(
    val id: String,
    val academicYear: String,
    val courseCode: String,
    val courseName: String,
    val type: PaperType,
    val yearOfStudy: Int = 1,
    val downloadCount: Int = 0,
    val pageCount: Int = 0,
    val tags: List<String> = emptyList(),
    val uploaderName: String = "",
    val uploadDate: String = "",
    val url: String = ""
)

enum class PaperType {
    EXAM,
    TEST;

    val displayName: String
        get() = when (this) {
            EXAM -> "Exam"
            TEST -> "Test"
        }
}
