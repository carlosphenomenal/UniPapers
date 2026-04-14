package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.feature.filemanagement.data.model.FileUploadDto
import com.unipapers.unipapers_frontend.core.domain.model.Course
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.GeminiResult
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.SuggestTagsUseCase
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.usecase.UploadFileUseCase
import com.unipapers.unipapers_frontend.feature.filemanagement.utils.FileHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed class UploadStatus {
    object Idle : UploadStatus()
    object Hashing : UploadStatus()
    object Loading : UploadStatus()
    data class Success(val message: String) : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}

@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val uploadFileUseCase: UploadFileUseCase,
    private val suggestTagsUseCase: SuggestTagsUseCase
) : ViewModel() {

    private val _status = mutableStateOf<UploadStatus>(UploadStatus.Idle)
    val status: State<UploadStatus> = _status

    private val _state = MutableStateFlow(UploadState())
    val uiState = _state.asStateFlow()

    private var geminiJob: Job? = null

    // Form data states
    val topicsNames = mutableStateListOf<String>()

    fun onCourseSelected(course: Course) {
        _state.update {
            it.copy(
                selectedCoursePublicId = course.publicId,
                selectedCourseUnit = course.courseCode,
                selectedCourseUnitName = course.courseName
            )
        }
    }

    fun onPaperTypeSelected(paperType: String) {
        _state.update { it.copy(selectedPaperType = paperType) }
    }

    fun onAcademicYearSelected(academicYear: String) {
        _state.update { it.copy(selectedAcademicYear = academicYear) }
    }

    fun onSemesterSelected(semester: String) {
        _state.update { it.copy(selectedSemester = semester) }
    }

    fun onYearOfStudySelected(yearOfStudy: String) {
        _state.update { it.copy(selectedYearOfStudy = yearOfStudy) }
    }

    fun onFileSelected(uri: Uri, name: String, size: String) {
        _state.update { it.copy(
            selectedFileUri = uri,
            selectedFileName = name,
            selectedFileSize = size
        ) }
    }

    fun uploadFile(file: File) {
        viewModelScope.launch {
            _status.value = UploadStatus.Hashing
            val fileHash = withContext(Dispatchers.IO) {
                FileHashUtil.sha256(file)
            }

            if (fileHash == null) {
                _status.value = UploadStatus.Error("Could not read the file. Please try again.")
                return@launch
            }

            val fileUploadDto = buildUploadDto(fileHash)

            _status.value = UploadStatus.Loading
            uploadFileUseCase(fileUploadDto, file)
                .onSuccess {
                    _status.value = UploadStatus.Success("File uploaded and confirmed successfully!")
                }
                .onFailure { error ->
                    _status.value = UploadStatus.Error(
                        error.localizedMessage ?: "An unknown error occurred"
                    )
                }
        }
    }

    fun buildUploadDto(fileHash: String): FileUploadDto {
        val currentState = _state.value
        return FileUploadDto(
            coursePublicId = currentState.selectedCoursePublicId,
            courseName = currentState.selectedCourseUnitName.ifBlank { currentState.selectedCourseUnit },
            fileName = currentState.selectedFileName,
            fileHash = fileHash,
            pastPaperType = currentState.selectedPaperType,
            academicYear = currentState.selectedAcademicYear,
            yearOfStudy = currentState.selectedYearOfStudy.replace("Year ", "").toIntOrNull() ?: 1,
            semester = when (currentState.selectedSemester) {
                "Semester 1" -> 1
                "Semester 2" -> 2
                else -> 1
            },
            topicsNames = topicsNames.toList()
        )
    }

    fun addTopic(topicName: String) {
        if (topicName.isNotBlank() && !topicsNames.contains(topicName)) {
            topicsNames.add(topicName)
            _state.update { it.copy(confirmedTags = it.confirmedTags + topicName) }
        }
    }

    fun removeTopic(topicName: String) {
        topicsNames.remove(topicName)
        _state.update { it.copy(confirmedTags = it.confirmedTags - topicName) }
    }

    fun toggleTag(tag: String) {
        _state.update { current ->
            if (current.confirmedTags.contains(tag)) {
                topicsNames.remove(tag)
                current.copy(confirmedTags = current.confirmedTags - tag)
            } else {
                topicsNames.add(tag)
                current.copy(confirmedTags = current.confirmedTags + tag)
            }
        }
    }

    fun resetState() {
        _status.value = UploadStatus.Idle
    }

    fun onStep1Next() {
        val uri = _state.value.selectedFileUri ?: return
        _state.update { it.copy(currentStep = 2) }
        startGeminiAnalysis(uri)
    }

    fun nextStep() {
        _state.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun previousStep() {
        _state.update { it.copy(currentStep = it.currentStep - 1) }
    }
    

    private fun startGeminiAnalysis(uri: Uri) {
        geminiJob?.cancel()
        _state.update { it.copy(isLoadingTags = true, tagError = null) }

        geminiJob = viewModelScope.launch {
            when (val result = suggestTagsUseCase(uri)) {
                is GeminiResult.Success -> {
                    val tags = result.response.topicTags
                    _state.update { current ->
                        current.copy(
                            isLoadingTags = false,
                            suggestedTags = tags,
                            confirmedTags = tags.toSet(),
                            geminiMetadata = result.response.metadata,
                            tagError = null
                        )
                    }
                    // Sync topicsNames list
                    topicsNames.clear()
                    topicsNames.addAll(tags)
                    
                    // Auto-fill metadata if confident
                    if (result.response.confidence == "high") {
                        result.response.metadata.courseName?.let { courseName ->
                            _state.update { it.copy(selectedCourseUnitName = courseName) }
                        }
                        result.response.metadata.academicYear?.let { academicYear ->
                            _state.update { it.copy(selectedAcademicYear = academicYear) }
                        }
                    }
                }
                is GeminiResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoadingTags = false,
                            suggestedTags = emptyList(),
                            tagError = result.message
                        )
                    }
                }
            }
        }
    }
}
