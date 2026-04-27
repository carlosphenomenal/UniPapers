package com.unipapers.unipapers_frontend.feature.browse.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.unipapers.unipapers_frontend.core.domain.model.DummyData
import com.unipapers.unipapers_frontend.core.domain.model.PaperType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BrowsePapersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseCode: String = checkNotNull(savedStateHandle["courseCode"])

    private val _state = MutableStateFlow(BrowsePapersState())
    val state: StateFlow<BrowsePapersState> = _state.asStateFlow()

    init {
        loadPapers()
    }

    private fun loadPapers() {
        val course = DummyData.courses.find { it.courseCode == courseCode }
        val allPapersForCourse = DummyData.papers.filter { it.courseCode == courseCode }
        
        _state.update { 
            it.copy(
                courseCode = courseCode,
                courseName = course?.courseName ?: "Unknown Course",
                paperCount = course?.paperCount ?: 0,
                papers = allPapersForCourse
            )
        }
    }

    fun onTypeSelected(type: PaperType) {
        _state.update { it.copy(selectedType = type) }
    }
}
