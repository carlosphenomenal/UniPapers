package com.unipapers.unipapers_frontend.feature.home.domain

data class Paper(
    val id: String,
    val title: String,
    val courseUnit: String,
    val type: PaperType,
    val academicYear: String,
    val uploadedBy: String,
    val timeAgo: String,
    val pageCount: Int = 0
)

data class CourseUnit(
    val code: String,
    val name: String,
    val paperCount: Int
)

data class ContinueStudyingItem(
    val paper: Paper,
    val progressPercent: Int
)

enum class PaperType(val displayName: String) {
    EXAM("Exam"),
    TEST_CAT("Test/CAT"),
    LECTURE_NOTES("Notes")
}
