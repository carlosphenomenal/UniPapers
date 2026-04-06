package com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase

import android.net.Uri
import com.unipapers.unipapers_frontend.feature.filemanagement.utils.GeminiService
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.GeminiResult
import javax.inject.Inject

class SuggestTagsUseCase @Inject constructor(
    private val geminiService: GeminiService
) {

    suspend operator fun invoke(uri: Uri): GeminiResult {
        return geminiService.analysePaper(uri)
    }
}
