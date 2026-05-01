package com.unipapers.unipapers_frontend.feature.auth.presentation.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyViewModel : ViewModel() {
    private val _state = MutableStateFlow(VerifyState())
    val state: StateFlow<VerifyState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        startResendTimer()
    }

    fun onCodeChange(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _state.update { it.copy(code = code) }
            if (code.length == 6) {
                verifyCode()
            }
        }
    }

    fun onResendCode() {
        if (_state.value.canResend) {
            _state.update { it.copy(resendTimer = 30, canResend = false) }
            startResendTimer()
            // TODO: Call API to resend code
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
            // TODO: Call API to verify code
            // On success: navigate to home
            // On failure: show error
            _state.update { it.copy(isLoading = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
