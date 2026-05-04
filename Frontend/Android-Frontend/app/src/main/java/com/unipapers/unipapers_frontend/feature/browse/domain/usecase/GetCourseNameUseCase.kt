package com.unipapers.unipapers_frontend.feature.browse.domain.usecase

import com.unipapers.unipapers_frontend.core.domain.repository.CourseRepository
import javax.inject.Inject

class GetCourseNameUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseCode: String): Result<String> {
        return repository.getCourses().map { courses ->
            courses.firstOrNull { it.courseCode == courseCode }?.courseName.orEmpty()
        }
    }
}

