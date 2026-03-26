package com.unipapers.unipapers_frontend.feature.upload.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.feature.upload.data.datasource.FileUploadDto
import com.unipapers.unipapers_frontend.feature.upload.domain.usecase.UploadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel() {

    private val _state = mutableStateOf<UploadState>(UploadState.Idle)
    val state: State<UploadState> = _state

    // Form data states
    val coursePublicId = mutableStateOf("")
    val courseName = mutableStateOf("")
    val fileName = mutableStateOf("")
    val type = mutableStateOf("")
    val academicYear = mutableStateOf("")
    val yearOfStudy = mutableIntStateOf(1)
    val semester = mutableIntStateOf(1)
    val topicsNames = mutableStateListOf<String>()

    fun uploadFile(file: File) {
        val fileUploadDto = FileUploadDto(
            coursePublicId = coursePublicId.value,
            courseName = courseName.value,
            fileName = fileName.value,
            pastPaperType = type.value,
            academicYear = academicYear.value,
            yearOfStudy = yearOfStudy.intValue,
            semester = semester.intValue,
            topicsNames = topicsNames.toList()
        )

        viewModelScope.launch {
            _state.value = UploadState.Loading
            uploadFileUseCase(fileUploadDto, file)
                .onSuccess {
                    _state.value = UploadState.Success("File uploaded and confirmed successfully!")
                }
                .onFailure { error ->
                    _state.value = UploadState.Error(error.localizedMessage ?: "An unknown error occurred")
                }
        }
    }

    fun addTopic(topicName: String) {
        if (topicName.isNotBlank()) {
            topicsNames.add(topicName)
        }
    }

    fun removeTopic(topicName: String) {
        topicsNames.remove(topicName)
    }

    fun resetState() {
        _state.value = UploadState.Idle
    }
}
