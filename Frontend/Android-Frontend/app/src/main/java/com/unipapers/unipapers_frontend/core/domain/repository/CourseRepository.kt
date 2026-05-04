package com.unipapers.unipapers_frontend.core.domain.repository

import com.unipapers.unipapers_frontend.core.domain.model.Course

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
}

