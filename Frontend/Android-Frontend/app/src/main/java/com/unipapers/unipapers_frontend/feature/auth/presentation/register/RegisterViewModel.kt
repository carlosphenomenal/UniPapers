package com.unipapers.unipapers_frontend.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SignupRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.GetProgramsUseCase
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val getProgramsUseCase: GetProgramsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class Success(val email: String) : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    init {
        loadPrograms()
    }

    private fun loadPrograms() {
        viewModelScope.launch {
            _state.update { it.copy(isProgramsLoading = true) }
            when (val result = getProgramsUseCase()) {
                is Resource.Success -> {
                    _state.update { it.copy(
                        programs = result.data ?: emptyList(),
                        isProgramsLoading = false,
                        programsError = null
                    ) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(
                        isProgramsLoading = false,
                        programsError = result.message ?: "Failed to load programs"
                    ) }
                    _eventFlow.emit(UiEvent.ShowSnackbar("Failed to load programs: ${result.message}"))
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isProgramsLoading = true) }
                }
            }
        }
    }

    fun onFirstNameChange(firstName: String) {
        _state.update { it.copy(firstName = firstName, error = null) }
    }

    fun onLastNameChange(lastName: String) {
        _state.update { it.copy(lastName = lastName, error = null) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onStudentNumberChange(studentNumber: String) {
        _state.update { it.copy(studentNumber = studentNumber.trim(), error = null) }
    }

     fun onProgrammeChange(programme: String, programmeId: String? = null) {
         val currentState = _state.value
         val selectedProgram = currentState.programs.find { it.publicId == (programmeId ?: "") }

         var error: String? = null
         var newYearOfStudy = currentState.yearOfStudy

         // Validate the current year against the new program's duration
         if (selectedProgram != null && currentState.yearOfStudy > selectedProgram.durationYears) {
             newYearOfStudy = 1
             error = "Year of study reset to 1 - program duration is ${selectedProgram.durationYears} years"
         }

         _state.update { it.copy(
             programme = programme,
             programmeId = programmeId ?: "",
             yearOfStudy = newYearOfStudy,
             error = error
         ) }
     }

     fun onYearOfStudyChange(year: Int) {
         val currentState = _state.value
         val selectedProgram = currentState.programs.find { it.publicId == currentState.programmeId }

         val error = if (selectedProgram != null && year > selectedProgram.durationYears) {
             "Year of study cannot exceed program duration (${selectedProgram.durationYears} years)"
         } else {
             null
         }

         _state.update { it.copy(yearOfStudy = year, error = error) }
     }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

      fun onSignUp() {
          viewModelScope.launch {
              val currentState = _state.value
              if (currentState.firstName.isBlank() || currentState.lastName.isBlank() ||
                  currentState.email.isBlank() || currentState.password.isBlank() ||
                  currentState.studentNumber.isBlank() || currentState.programmeId.isBlank()) {
                  _eventFlow.emit(UiEvent.ShowSnackbar("Please fill all fields"))
                  return@launch
              }

              val studentNumberTrimmed = currentState.studentNumber.trim()
              val studentNum = studentNumberTrimmed.toLongOrNull()
              if (studentNum == null) {
                  _eventFlow.emit(UiEvent.ShowSnackbar("Invalid student number"))
                  return@launch
              }

              // Validate year of study against program duration
              val selectedProgram = currentState.programs.find { it.publicId == currentState.programmeId }
              if (selectedProgram != null && currentState.yearOfStudy > selectedProgram.durationYears) {
                  _state.update { it.copy(error = "Year of study cannot exceed program duration (${selectedProgram.durationYears} years)") }
                  _eventFlow.emit(UiEvent.ShowSnackbar("Invalid year of study for selected program"))
                  return@launch
              }

              _state.update { it.copy(isLoading = true) }
              val result = registerUseCase(
                  SignupRequestDto(
                      firstName = currentState.firstName.trim(),
                      lastName = currentState.lastName.trim(),
                      email = currentState.email.trim(),
                      studentNumber = studentNum,
                      password = currentState.password.trim(),
                      programmePublicId = currentState.programmeId, // Use the ID
                      yearOfStudy = currentState.yearOfStudy
                  )
              )

             when (result) {
                 is Resource.Success -> {
                     _state.update { it.copy(isLoading = false) }
                     _eventFlow.emit(UiEvent.Success(currentState.email))
                 }
                 is Resource.Error -> {
                     _state.update { it.copy(isLoading = false, error = result.message) }
                     _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "An unknown error occurred"))
                 }
                 is Resource.Loading -> {
                     _state.update { it.copy(isLoading = true) }
                 }
             }
         }
     }
}