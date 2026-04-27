package com.unipapers.unipapers_frontend.feature.browse.presentation

import androidx.lifecycle.ViewModel
import com.unipapers.unipapers_frontend.core.domain.model.DummyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CourseUnitViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CourseUnitState())
    val state: StateFlow<CourseUnitState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _state.value = CourseUnitState(
            totalPapers = DummyData.TOTAL_PAPERS,
            totalCourses = DummyData.TOTAL_COURSES,
            totalExams = DummyData.TOTAL_EXAMS,
            courses = DummyData.courses
        )
    }
}
