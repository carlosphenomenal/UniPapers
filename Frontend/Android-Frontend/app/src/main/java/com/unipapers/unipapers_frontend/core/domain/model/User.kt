package com.unipapers.unipapers_frontend.core.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val studentNumber: String,
    val programme: String,
    val yearOfStudy: Int,
    val currentSemester: Int,
    val freeViewsRemaining: Int,
    val hasUnlockedAccess: Boolean,
    val uploadCount: Int
)

