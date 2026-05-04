package com.unipapers.unipapers_frontend.feature.auth.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.util.Resource
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.AuthenticateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StartupState())
    val state = _state.asStateFlow()

    init {
        authenticate()
    }

    private fun authenticate() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isChecking = true)
            when (authenticateUseCase()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isChecking = false,
                        result = StartupAuthResult.Authenticated
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isChecking = false,
                        result = StartupAuthResult.Unauthenticated
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

enum class StartupAuthResult {
    Authenticated,
    Unauthenticated
}

data class StartupState(
    val isChecking: Boolean = true,
    val result: StartupAuthResult? = null
)
