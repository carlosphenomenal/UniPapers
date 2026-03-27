package com.unipapers.unipapers_frontend.feature.upload.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadDto
import com.unipapers.unipapers_frontend.feature.upload.domain.usecase.UploadFileUseCase
import com.unipapers.unipapers_frontend.feature.upload.utils.FileHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed class UploadState {
    object Idle : UploadState()
    object Hashing : UploadState()
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
        viewModelScope.launch {
            // Compute the hash on the IO thread so the UI stays responsive
            _state.value = UploadState.Hashing
            val fileHash = withContext(Dispatchers.IO) {
                FileHashUtil.sha256(file)
            }

            if (fileHash == null) {
                _state.value = UploadState.Error("Could not read the file. Please try again.")
                return@launch
            }

            // Build the DTO
            val fileUploadDto = FileUploadDto(
                coursePublicId = coursePublicId.value,
                courseName = courseName.value,
                fileName = fileName.value,
                fileHash = fileHash,
                pastPaperType = type.value,
                academicYear = academicYear.value,
                yearOfStudy = yearOfStudy.intValue,
                semester = semester.intValue,
                topicsNames = topicsNames.toList()
            )

            // Hand off to the use-case
            _state.value = UploadState.Loading
            uploadFileUseCase(fileUploadDto, file)
                .onSuccess {
                    _state.value = UploadState.Success("File uploaded and confirmed successfully!")
                }
                .onFailure { error ->
                    _state.value = UploadState.Error(
                        error.localizedMessage ?: "An unknown error occurred"
                    )
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
