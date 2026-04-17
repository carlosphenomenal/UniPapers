package com.unipapers.unipapers_frontend.feature.home.domain

data class Paper(
    val id: String,
    val academicYear: String,
    val courseCode: String,
    val courseName: String,
    val type: PaperType
)

enum class PaperType(val displayName: String) {
    EXAM("Exam"),
    TEST("Test"),
    ASSIGNMENT("Assignment"),
    NOTES("Notes")
}
