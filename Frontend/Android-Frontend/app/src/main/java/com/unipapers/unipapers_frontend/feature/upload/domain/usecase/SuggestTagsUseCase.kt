package com.unipapers.unipapers_frontend.feature.upload.domain.usecase

import android.net.Uri
import com.unipapers.unipapers_frontend.core.data.remote.GeminiService
import com.unipapers.unipapers_frontend.feature.upload.domain.model.GeminiResult
import javax.inject.Inject

class SuggestTagsUseCase @Inject constructor(
    private val geminiService: GeminiService
) {

    suspend operator fun invoke(uri: Uri): GeminiResult {
        return geminiService.analysePaper(uri)
    }
}
