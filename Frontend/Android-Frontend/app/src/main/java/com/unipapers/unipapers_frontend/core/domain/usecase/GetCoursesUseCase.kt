package com.unipapers.unipapers_frontend.core.domain.usecase

import com.unipapers.unipapers_frontend.core.domain.model.Course
import com.unipapers.unipapers_frontend.core.domain.repository.CourseRepository
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(): Result<List<Course>> {
        return repository.getCourses()
    }
}

