package com.unipapers.unipapers_frontend.feature.auth.presentation.forgot

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.SendCodeUseCase
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordVerifyViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val sendCodeUseCase: SendCodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordVerifyState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var timerJob: Job? = null
    private val email: String = savedStateHandle.get<String>("email").orEmpty()

    sealed class UiEvent {
        data class NavigateToReset(val email: String) : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    init {
        _state.update { it.copy(email = email) }
        startResendTimer()
    }

    fun onCodeChange(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _state.update { it.copy(code = code, error = null) }
            if (code.length == 6) {
                verifyCode()
            }
        }
    }

    fun onResendCode() {
        if (!_state.value.canResend) return
        viewModelScope.launch {
            when (val result = sendCodeUseCase(email)) {
                is Resource.Success -> {
                    _state.update { it.copy(resendTimer = 30, canResend = false) }
                    startResendTimer()
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.data?.message ?: "Verification code resent"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to resend code"))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun verifyCode() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (
                val result = verifyEmailUseCase(
                    VerifyEmailRequestDto(email = email, verificationCode = _state.value.code)
                )
            ) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _eventFlow.emit(UiEvent.NavigateToReset(email))
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.resendTimer > 0) {
                delay(1000L)
                _state.update { it.copy(resendTimer = it.resendTimer - 1) }
            }
            _state.update { it.copy(canResend = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

