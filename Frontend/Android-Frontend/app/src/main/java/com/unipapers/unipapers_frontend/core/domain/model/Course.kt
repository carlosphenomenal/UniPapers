package com.unipapers.unipapers_frontend.core.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

data class Course(
    val courseCode: String,
    val courseName: String = "",
    val paperCount: Int = 0,
    val icon: ImageVector = Icons.Default.School,
    val publicId: String = ""
)
