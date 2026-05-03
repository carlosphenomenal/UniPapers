package com.unipapers.unipapers_frontend.feature.auth.presentation.verify

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.SignupRequestDto
import com.unipapers.unipapers_frontend.feature.auth.data.remote.dto.VerifyEmailRequestDto
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.ResendCodeUseCase
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(VerifyState())
    val state: StateFlow<VerifyState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var timerJob: Job? = null
    private var email: String = ""

    init {
        email = savedStateHandle.get<String>("email") ?: ""
        _state.update { it.copy(email = email) }
        startResendTimer()
    }

    sealed class UiEvent {
        object Success : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
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
        if (_state.value.canResend) {
            viewModelScope.launch {
                // Backend expects SignupRequestDto for resend-verification-code even if only email is used
                val signupDto = SignupRequestDto(
                    firstName = "", lastName = "", email = email,
                    studentNumber = 0, password = "", programmePublicId = "", yearOfStudy = 0
                )
                when (val result = resendCodeUseCase(email)) {
                    is Resource.Success -> {
                        _state.update { it.copy(resendTimer = 30, canResend = false) }
                        startResendTimer()
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.data ?: "Verification code resent"))
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to resend code"))
                    }
                    is Resource.Loading -> {}
                }
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

    private fun verifyCode() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = verifyEmailUseCase(
                VerifyEmailRequestDto(email = email, verificationCode = _state.value.code)
            )
            when (result) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success)
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
