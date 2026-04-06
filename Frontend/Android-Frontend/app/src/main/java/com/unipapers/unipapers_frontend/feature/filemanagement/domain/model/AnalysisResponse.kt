package com.unipapers.unipapers_frontend.feature.filemanagement.domain.model

data class AnalysisResponse(
    val topicTags: List<String>,
    val metadata: PaperMetadata,
    val confidence: String,
    val rawQuestionsSummary: String
)
data class PaperMetadata(
    val courseName: String? = null,
    val courseCode: String? = null,
    val programmeName: String? = null,
    val yearOfStudy: String? = null,
    val academicYear: String? = null,
    val semester: String? = null,
    val paperType: String? = null,
    val institution: String? = null
)

sealed class GeminiResult {
    data class Success(val response: AnalysisResponse) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
}
