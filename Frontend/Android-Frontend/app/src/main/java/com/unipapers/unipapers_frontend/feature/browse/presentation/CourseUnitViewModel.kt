package com.unipapers.unipapers_frontend.feature.browse.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.domain.usecase.GetCoursesUseCase
import com.unipapers.unipapers_frontend.feature.browse.domain.usecase.GetPapersByCourseUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseUnitViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getPapersByCourseUnitUseCase: GetPapersByCourseUnitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CourseUnitState())
    val state: StateFlow<CourseUnitState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getCoursesUseCase()
                .onSuccess { courses ->
                    // 1. Emit the course list immediately so UI is not empty
                    _state.update { it.copy(
                        courses = courses,
                        totalCourses = courses.size,
                        isLoading = false,
                        error = null
                    ) }

                    // 2. Fetch counts in background and update incrementally
                    viewModelScope.launch {
                        val coursesWithCounts = courses.map { course ->
                            async {
                                val paperCount = getPapersByCourseUnitUseCase(course.courseCode)
                                    .getOrNull()?.size ?: 0
                                course.copy(paperCount = paperCount)
                            }
                        }.awaitAll()

                        _state.update { it.copy(
                            courses = coursesWithCounts,
                            totalPapers = coursesWithCounts.sumOf { it.paperCount },
                            totalExams = coursesWithCounts.count { it.paperCount > 0 }
                        ) }
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load courses"
                        )
                    }
                }
        }
    }
}
