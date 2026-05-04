package com.unipapers.unipapers_frontend.feature.browse.domain.usecase

import com.unipapers.unipapers_frontend.core.domain.model.Paper
import com.unipapers.unipapers_frontend.core.domain.model.PaperType
import com.unipapers.unipapers_frontend.feature.browse.domain.repository.BrowseRepository
import javax.inject.Inject

class GetPapersByCourseUnitUseCase @Inject constructor(
	private val repository: BrowseRepository
) {
	suspend operator fun invoke(courseCode: String, filter: PaperType? = null): Result<List<Paper>> {
		return repository.getPastPapers(courseCode, filter)
	}
}
