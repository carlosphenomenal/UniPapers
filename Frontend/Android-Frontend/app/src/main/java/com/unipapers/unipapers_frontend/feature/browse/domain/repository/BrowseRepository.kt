package com.unipapers.unipapers_frontend.feature.browse.domain.repository

import com.unipapers.unipapers_frontend.core.domain.model.Paper
import com.unipapers.unipapers_frontend.core.domain.model.PaperType

interface BrowseRepository {
    suspend fun getPastPapers(courseCode: String, filter: PaperType?): Result<List<Paper>>
    suspend fun getPaperSignedUrl(paperId: String): Result<String>
}

