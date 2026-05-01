package com.unipapers.unipapers_frontend.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onFirstNameChange(firstName: String) {
        _state.update { it.copy(firstName = firstName) }
    }

    fun onLastNameChange(lastName: String) {
        _state.update { it.copy(lastName = lastName) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onStudentNumberChange(studentNumber: String) {
        _state.update { it.copy(studentNumber = studentNumber) }
    }

    fun onProgrammeChange(programme: String) {
        _state.update { it.copy(programme = programme) }
    }

    fun onYearOfStudyChange(year: Int) {
        _state.update { it.copy(yearOfStudy = year) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSignUp() {
        // TODO: Handle sign up logic
    }
}
