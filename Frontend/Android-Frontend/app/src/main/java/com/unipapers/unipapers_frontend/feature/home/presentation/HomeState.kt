package com.unipapers.unipapers_frontend.feature.home.presentation

import com.unipapers.unipapers_frontend.feature.home.domain.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType

data class HomeState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: PaperType? = null,
    val recentPapers: List<Paper> = emptyList(),
    val papers: List<Paper> = emptyList(),
    val errorMessage: String? = null
)
