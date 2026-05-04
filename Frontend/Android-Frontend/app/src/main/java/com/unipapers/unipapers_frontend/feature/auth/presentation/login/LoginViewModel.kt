package com.unipapers.unipapers_frontend.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.LoginRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.LoginUseCase
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.SendCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sendCodeUseCase: SendCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        object Success : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
        data class UnverifiedAccount(val email: String, val message: String) : UiEvent()
        data class NavigateToVerify(val email: String) : UiEvent()
    }

    fun onIdentifierChange(identifier: String) {
        _state.update { it.copy(identifier = identifier, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSignIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = loginUseCase(
                LoginRequestDto(
                    identifier = _state.value.identifier,
                    password = _state.value.password
                )
            )
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _eventFlow.emit(UiEvent.Success)
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    val message = result.message ?: "An unknown error occurred"
                    if (isUnverifiedMessage(message)) {
                        _eventFlow.emit(
                            UiEvent.UnverifiedAccount(
                                email = _state.value.identifier,
                                message = message
                            )
                        )
                    } else {
                        _eventFlow.emit(UiEvent.ShowSnackbar(message))
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onSendVerificationCode(identifier: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = sendCodeUseCase(identifier.trim())
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _eventFlow.emit(UiEvent.NavigateToVerify(result.data?.email ?: identifier))
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to send verification code"))
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun isUnverifiedMessage(message: String): Boolean {
        val normalized = message.lowercase()
        return normalized.contains("not verified") ||
            normalized.contains("verify your email") ||
            normalized.contains("account is disabled")
    }
}
