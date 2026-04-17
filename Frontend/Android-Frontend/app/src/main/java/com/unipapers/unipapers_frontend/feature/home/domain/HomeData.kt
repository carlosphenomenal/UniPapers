package com.unipapers.unipapers_frontend.feature.home.data

import com.unipapers.unipapers_frontend.feature.home.domain.ContinueStudyingItem
import com.unipapers.unipapers_frontend.feature.home.domain.CourseUnit
import com.unipapers.unipapers_frontend.feature.home.domain.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType

val dummyPapers = listOf(
    Paper(
        id = "1",
        title = "Final Exam 2023/2024 Sem 1",
        courseUnit = "CSC 2100",
        type = PaperType.EXAM,
        academicYear = "2023/2024",
        uploadedBy = "Joshua M.",
        timeAgo = "2 days ago",
        pageCount = 8
    ),
    Paper(
        id = "2",
        title = "CAT 2 - Semester 1",
        courseUnit = "CSC 1200",
        type = PaperType.TEST_CAT,
        academicYear = "2023/2024",
        uploadedBy = "Sarah K.",
        timeAgo = "3 days ago",
        pageCount = 4
    ),
    Paper(
        id = "3",
        title = "Final Exam 2022/2023 Sem 2",
        courseUnit = "CSC 3100",
        type = PaperType.EXAM,
        academicYear = "2022/2023",
        uploadedBy = "Brian O.",
        timeAgo = "1 week ago",
        pageCount = 10
    ),
    Paper(
        id = "4",
        title = "CAT 1 - Semester 2",
        courseUnit = "CSC 2200",
        type = PaperType.TEST_CAT,
        academicYear = "2023/2024",
        uploadedBy = "Grace N.",
        timeAgo = "5 days ago",
        pageCount = 3
    ),
    Paper(
        id = "5",
        title = "Lecture Notes - Week 1-6",
        courseUnit = "CSC 1100",
        type = PaperType.LECTURE_NOTES,
        academicYear = "2023/2024",
        uploadedBy = "Peter M.",
        timeAgo = "2 weeks ago",
        pageCount = 24
    )
)

val dummyCourseUnits = listOf(
    CourseUnit("CSC 1100", "Introduction to Computer Science", 12),
    CourseUnit("CSC 1200", "Programming Fundamentals", 8),
    CourseUnit("CSC 2100", "Data Structures & Algorithms", 15),
    CourseUnit("CSC 2200", "Database Systems", 10),
    CourseUnit("CSC 3100", "Software Engineering", 7),
    CourseUnit("CSC 3200", "Computer Networks", 9)
)

val dummyContinueStudying = listOf(
    ContinueStudyingItem(
        paper = Paper(
            id = "2",
            title = "CAT 2 - Semester 1",
            courseUnit = "CSC 1200",
            type = PaperType.TEST_CAT,
            academicYear = "2023/2024",
            uploadedBy = "Sarah K.",
            timeAgo = "3 days ago"
        ),
        progressPercent = 65
    ),
    ContinueStudyingItem(
        paper = Paper(
            id = "3",
            title = "Final Exam 2022/2023 Sem 2",
            courseUnit = "CSC 3100",
            type = PaperType.EXAM,
            academicYear = "2022/2023",
            uploadedBy = "Brian O.",
            timeAgo = "1 week ago"
        ),
        progressPercent = 30
    )
)