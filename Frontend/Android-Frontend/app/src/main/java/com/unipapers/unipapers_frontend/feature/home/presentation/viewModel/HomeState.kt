package com.unipapers.unipapers_frontend.feature.home.presentation.viewModel

import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType

data class HomeState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: PaperType? = null,
    val recentPapers: List<Paper> = emptyList(),
    val papers: List<Paper> = emptyList(),
    val errorMessage: String? = null
)
