package com.unipapers.unipapers_frontend.feature.auth.presentation.forgot

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.UpdatePasswordRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordResetViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordResetState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val email: String = savedStateHandle.get<String>("email").orEmpty()

    sealed class UiEvent {
        data class Success(val message: String) : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    init {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _state.update { it.copy(confirmPassword = password, error = null) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onResetPassword() {
        val password = _state.value.password.trim()
        val confirmPassword = _state.value.confirmPassword.trim()
        val error = when {
            password.isBlank() -> "New password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = updatePasswordUseCase(UpdatePasswordRequestDto(email, password))) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _eventFlow.emit(UiEvent.Success(result.data ?: "Password updated"))
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to update password"))
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}

