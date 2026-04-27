package com.unipapers.unipapers_frontend.feature.browse.presentation

import com.unipapers.unipapers_frontend.core.domain.model.Course

data class CourseUnitState(
    val totalPapers: Int = 0,
    val totalCourses: Int = 0,
    val totalExams: Int = 0,
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
