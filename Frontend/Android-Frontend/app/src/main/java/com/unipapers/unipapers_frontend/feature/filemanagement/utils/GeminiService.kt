package com.unipapers.unipapers_frontend.feature.filemanagement.utils

import android.content.Context
import android.net.Uri
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.AnalysisResponse
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.GeminiResult
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.PaperMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

// ─── Prompt ───────────────────────────────────────────────────

private const val EXTRACTION_PROMPT = """
You are an academic document analyser. You have been given a university exam paper or lecture notes PDF.

Your job is to extract two things:

1. METADATA — information printed on or clearly implied by the paper:
   - course_name: full name of the course/subject (e.g. "Data Structures and Algorithms")
   - course_code: the unit code (e.g. "CSC 2100")
   - programme_name: the degree programme (e.g. "BSc Computer Science")
   - year_of_study: which year this paper is for (e.g. "Year 2")
   - academic_year: the academic year (e.g. "2023/2024")
   - semester: which semester (e.g. "Semester 1")
   - paper_type: one of — "Exam", "Test"
   - institution: the university name if visible

2. TOPIC TAGS — a list of specific academic topics that the questions in this paper cover.
   - Be specific: "Binary Trees" not just "Data Structures"
   - Include 5 to 12 tags depending on the breadth of the paper
   - Tags should be concise, 1 to 4 words each
   - Base tags strictly on what is actually in the paper, not general course knowledge

3. RAW QUESTIONS SUMMARY — a one sentence summary of what the paper broadly tests.

4. CONFIDENCE — your confidence in the metadata extraction: "high", "medium", or "low".
   Use "low" if key fields like course name or code were not clearly printed on the paper.

Respond ONLY with a valid JSON object in this exact structure. No preamble, no explanation, no markdown, no code fences, no ```json, no ``` — raw JSON only, starting with { and ending with }:

{
  "metadata": {
    "course_name": "...",
    "course_code": "...",
    "programme_name": "...",
    "year_of_study": "...",
    "academic_year": "...",
    "semester": "...",
    "paper_type": "...",
    "institution": "..."
  },
  "topic_tags": ["tag1", "tag2", "tag3"],
  "raw_questions_summary": "...",
  "confidence": "high"
}

If a metadata field is not present or cannot be determined from the document, use null for that field.
Do not guess or invent values. Only extract what is clearly present.
"""

// ─── Service ──────────────────────────────────────────────────

@Singleton
class GeminiService @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("gemini_api_key") private val apiKey: String
) {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.1f
            maxOutputTokens = 4096
        }
    )

    suspend fun analysePaper(fileUri: Uri): GeminiResult = withContext(Dispatchers.IO) {

        // ── Read file bytes from URI ──────────────────────────
        val bytes = try {
            context.contentResolver.openInputStream(fileUri)?.use { it.readBytes() }
                ?: return@withContext GeminiResult.Error("Could not open file at URI.")
        } catch (e: Exception) {
            return@withContext GeminiResult.Error("Failed to read file: ${e.message}")
        }

        // ── Validate file is not empty ────────────────────────
        if (bytes.isEmpty()) {
            return@withContext GeminiResult.Error("Uploaded file is empty.")
        }

        // ── Determine MIME type ───────────────────────────────
        val mimeType = context.contentResolver.getType(fileUri) ?: "application/pdf"

        // ── Send to Gemini ────────────────────────────────────
        val raw = try {
            val response = model.generateContent(
                content {
                    blob(mimeType, bytes)
                    text(EXTRACTION_PROMPT)
                }
            )
            response.text ?: return@withContext GeminiResult.Error("Gemini returned empty response.")
        } catch (e: Exception) {
            return@withContext GeminiResult.Error("Gemini API error: ${e.message}")
        }

        // ── Strip markdown fences if Gemini wraps anyway ──────
        val cleaned = raw.trim()
            .replace(Regex("^```[a-zA-Z]*\\n?"), "")
            .replace(Regex("\\n?```$"), "")
            .trim()

        // ── Parse JSON ────────────────────────────────────────
        return@withContext try {
            val parsed = JSONObject(cleaned)

            // Tags — validate it is an array, cap at 15
            val tagsArray = parsed.optJSONArray("topic_tags")
            val tags = mutableListOf<String>()
            if (tagsArray != null) {
                for (i in 0 until minOf(tagsArray.length(), 15)) {
                    val tag = tagsArray.optString(i).trim()
                    if (tag.isNotEmpty()) tags.add(tag)
                }
            }

            // Metadata — null-safe extraction
            val metaObj = parsed.optJSONObject("metadata")
            val metadata = PaperMetadata(
                courseName     = metaObj?.optString("course_name")?.takeIf { it != "null" && it.isNotBlank() },
                courseCode     = metaObj?.optString("course_code")?.takeIf { it != "null" && it.isNotBlank() },
                programmeName  = metaObj?.optString("programme_name")?.takeIf { it != "null" && it.isNotBlank() },
                yearOfStudy    = metaObj?.optString("year_of_study")?.takeIf { it != "null" && it.isNotBlank() },
                academicYear   = metaObj?.optString("academic_year")?.takeIf { it != "null" && it.isNotBlank() },
                semester       = metaObj?.optString("semester")?.takeIf { it != "null" && it.isNotBlank() },
                paperType      = metaObj?.optString("paper_type")?.takeIf { it != "null" && it.isNotBlank() },
                institution    = metaObj?.optString("institution")?.takeIf { it != "null" && it.isNotBlank() }
            )

            GeminiResult.Success(
                AnalysisResponse(
                    topicTags           = tags,
                    metadata            = metadata,
                    confidence          = parsed.optString("confidence", "medium"),
                    rawQuestionsSummary = parsed.optString("raw_questions_summary", "")
                )
            )
        } catch (e: Exception) {
            GeminiResult.Error(
                "Gemini returned unparseable JSON: ${e.message}. " +
                        "Raw response was: ${cleaned.take(300)}"
            )
        }
    }
}
