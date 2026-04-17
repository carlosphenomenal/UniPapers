package com.unipapers.unipapers_frontend.feature.home.domain.repository

import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType

interface HomeRepository {
    suspend fun getPastPapers(query: String?, filter: PaperType?): Result<List<Paper>>
    suspend fun getSignedUrl(paperId: String): Result<String>
}
