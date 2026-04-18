package com.unipapers.unipapers_frontend.feature.home.domain.model

data class Paper(
    val id: String,
    val academicYear: String,
    val courseCode: String,
    val courseName: String,
    val type: PaperType
)

enum class PaperType {
    EXAM,
    TEST,
    ASSIGNMENT,
    NOTES;

    val pluralName: String
        get() = when (this) {
            EXAM -> "exams"
            TEST -> "tests"
            ASSIGNMENT -> "assignments"
            NOTES -> "notes"
        }
}
