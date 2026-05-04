package com.unipapers.unipapers_frontend.feature.browse.presentation

import com.unipapers.unipapers_frontend.core.domain.model.Paper
import com.unipapers.unipapers_frontend.core.domain.model.PaperType

data class BrowsePapersState(
    val courseCode: String = "",
    val courseName: String = "",
    val paperCount: Int = 0,
    val selectedType: PaperType = PaperType.EXAM,
    val papers: List<Paper> = emptyList(),
    val isLoading: Boolean = false,
    val isOpeningPdf: Boolean = false,
    val pendingPdfUrl: String? = null,
    val error: String? = null
)
