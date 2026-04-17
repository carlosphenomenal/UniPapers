package com.unipapers.unipapers_frontend.feature.home.domain.usecase

import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetPastPapersUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(query: String? = null, filter: PaperType? = null): Result<List<Paper>> {
        return repository.getPastPapers(query, filter)
    }
}
