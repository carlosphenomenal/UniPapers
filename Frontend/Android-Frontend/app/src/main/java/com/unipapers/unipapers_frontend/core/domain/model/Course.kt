package com.unipapers.unipapers_frontend.core.domain.model

data class Course(
	val publicId: String,
	val courseCode: String,
	val courseName: String = courseCode
)

