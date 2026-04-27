package com.unipapers.unipapers_frontend.core.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material.icons.filled.Storage

object DummyData {
    val courses = listOf(
        Course(courseCode = "CSC 1100", courseName = "Introduction to Computer Science", paperCount = 1, icon = Icons.Default.Memory, publicId = "01JCSC1100000000000000001"),
        Course(courseCode = "CSC 1200", courseName = "Programming Fundamentals", paperCount = 1, icon = Icons.Default.Code, publicId = "01JCSC1200000000000000002"),
        Course(courseCode = "CSC 2100", courseName = "Data Structures & Algorithms", paperCount = 2, icon = Icons.Default.Schema, publicId = "01JCSC2100000000000000003"),
        Course(courseCode = "CSC 2200", courseName = "Database Systems", paperCount = 2, icon = Icons.Default.Storage, publicId = "01JCSC2200000000000000004"),
        Course(courseCode = "CSC 3100", courseName = "Software Engineering", paperCount = 2, icon = Icons.Default.Layers, publicId = "01JCSC3100000000000000005"),
        Course(courseCode = "CSC 3200", courseName = "Operating Systems", paperCount = 2, icon = Icons.Default.Memory, publicId = "01JCSC3200000000000000006"),
        Course(courseCode = "CSC 4100", courseName = "Artificial Intelligence", paperCount = 1, icon = Icons.Default.Schema, publicId = "01JCSC4100000000000000007"),
        Course(courseCode = "CSC 4200", courseName = "Computer Networks", paperCount = 1, icon = Icons.Default.Layers, publicId = "01JCSC4200000000000000008")
    )

    val papers = listOf(
        Paper(
            id = "1",
            academicYear = "2023/2024",
            courseCode = "CSC 2100",
            courseName = "CSC 2100 Final Examination 2023/2024",
            type = PaperType.EXAM,
            yearOfStudy = 2,
            downloadCount = 234,
            pageCount = 4,
            tags = listOf("Binary Trees", "Sorting Algorithms", "Big-O Notation"),
            uploaderName = "Alice K.",
            uploadDate = "2024-01-15"
        ),
        Paper(
            id = "2",
            academicYear = "2022/2023",
            courseCode = "CSC 2100",
            courseName = "CSC 2100 CAT 1 2022/2023",
            type = PaperType.TEST,
            yearOfStudy = 2,
            downloadCount = 120,
            pageCount = 2,
            tags = listOf("Stacks", "Queues"),
            uploaderName = "John D.",
            uploadDate = "2023-11-20"
        ),
        Paper(
            id = "3",
            academicYear = "2023/2024",
            courseCode = "CSC 2200",
            courseName = "CSC 2200 Final Exam 2023/2024",
            type = PaperType.EXAM,
            yearOfStudy = 2,
            downloadCount = 450,
            pageCount = 5,
            tags = listOf("SQL", "Normalization", "Indexing"),
            uploaderName = "Bob M.",
            uploadDate = "2024-05-10"
        )
    )

    const val TOTAL_PAPERS = 12
    const val TOTAL_COURSES = 8
    const val TOTAL_EXAMS = 6
}
