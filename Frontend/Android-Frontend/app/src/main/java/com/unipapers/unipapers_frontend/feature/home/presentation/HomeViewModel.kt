package com.unipapers.unipapers_frontend.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.unipapers.unipapers_frontend.feature.home.data.dummyContinueStudying
import com.unipapers.unipapers_frontend.feature.home.data.dummyCourseUnits
import com.unipapers.unipapers_frontend.feature.home.data.dummyPapers
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _state.update { currentState ->
            currentState.copy(
                papers = dummyPapers,
                filteredPapers = dummyPapers,
                courseUnits = dummyCourseUnits,
                continueStudying = dummyContinueStudying
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }

    fun onFilterSelected(filter: PaperType?) {
        val filtered = if (filter == null) {
            _state.value.papers
        } else {
            _state.value.papers.filter { it.type == filter }
        }
        _state.update { currentState ->
            currentState.copy(
                selectedFilter = filter,
                filteredPapers = filtered
            )
        }
    }
}