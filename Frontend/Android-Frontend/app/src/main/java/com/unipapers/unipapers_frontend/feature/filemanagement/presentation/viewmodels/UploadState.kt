package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.viewmodels


import android.net.Uri
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.PaperMetadata

data class UploadState(

    // ── Navigation ────────────────────────────────────────────
    val currentStep: Int = 1,

    // ── Step 1 — File selection ───────────────────────────────
    val selectedFileUri: Uri? = null,
    val selectedFileName: String = "",
    val selectedFileSize: String = "",

    // ── Step 2 — Paper details ────────────────────────────────
    val selectedCourseUnit: String = "",
    val selectedCourseUnitName: String = "",
    val selectedPaperType: String = "",
    val selectedAcademicYear: String = "",
    val selectedSemester: String = "",
    val selectedYearOfStudy: String = "",

    // ── Step 3 — Tag selection ────────────────────────────────
    // Gemini call fires in background when user leaves step 1.
    // These fields reflect wherever the call is at when step 3 renders.
    val isLoadingTags: Boolean = false,
    val suggestedTags: List<String> = emptyList(),
    val confirmedTags: Set<String> = emptySet(),
    val rejectedTags: Set<String> = emptySet(),
    val manualTagInput: String = "",
    val tagError: String? = null,

    // Metadata Gemini extracted — used to pre-fill step 2 fields
    // if confidence is high. Never shown directly to the user.
    val geminiMetadata: PaperMetadata? = null,

    // ── Step 4 — Review & submit ──────────────────────────────
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,

    // ── Global error ──────────────────────────────────────────
    val errorMessage: String? = null
) {
    // Derived helpers the UI can use directly

    val allConfirmedTags: List<String>
        get() = confirmedTags.toList()

    val hasEnoughTags: Boolean
        get() = confirmedTags.size >= 3

    val canProceedFromStep1: Boolean
        get() = selectedFileUri != null

    val canProceedFromStep2: Boolean
        get() = selectedCourseUnit.isNotBlank() &&
                selectedPaperType.isNotBlank() &&
                selectedAcademicYear.isNotBlank() &&
                selectedSemester.isNotBlank() &&
                selectedYearOfStudy.isNotBlank()

    val canProceedFromStep3: Boolean
        get() = hasEnoughTags
}
