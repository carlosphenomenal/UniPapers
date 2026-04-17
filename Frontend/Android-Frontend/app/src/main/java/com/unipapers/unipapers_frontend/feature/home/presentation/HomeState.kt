package com.unipapers.unipapers_frontend.feature.home.presentation

import com.unipapers.unipapers_frontend.feature.home.domain.ContinueStudyingItem
import com.unipapers.unipapers_frontend.feature.home.domain.CourseUnit
import com.unipapers.unipapers_frontend.feature.home.domain.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val papers: List<Paper> = emptyList(),
    val filteredPapers: List<Paper> = emptyList(),
    val courseUnits: List<CourseUnit> = emptyList(),
    val continueStudying: List<ContinueStudyingItem> = emptyList(),
    val selectedFilter: PaperType? = null,
    val searchQuery: String = "",
    val isOffline: Boolean = false,
    val errorMessage: String? = null
)
