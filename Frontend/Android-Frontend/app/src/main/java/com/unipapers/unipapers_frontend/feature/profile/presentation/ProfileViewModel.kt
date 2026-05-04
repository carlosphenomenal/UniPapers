package com.unipapers.unipapers_frontend.feature.profile.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.unipapers.unipapers_frontend.feature.auth.domain.usecase.LogoutUseCase
import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileResponse
import com.unipapers.unipapers_frontend.feature.profile.domain.usecase.GetProfileUseCase
import com.unipapers.unipapers_frontend.feature.profile.domain.usecase.UpdateNotificationPrefsUseCase
import com.unipapers.unipapers_frontend.feature.profile.domain.usecase.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val logoutUseCase: LogoutUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val updateNotificationPrefsUseCase: UpdateNotificationPrefsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadMockProfile()
    }

    private fun loadMockProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val profileData = withContext(Dispatchers.IO) {
                    val jsonString = context.assets.open("profile_mock.json")
                        .bufferedReader()
                        .use { it.readText() }
                    gson.fromJson(jsonString, ProfileResponse::class.java)
                }
                _state.update {
                    it.copy(isLoading = false, profile = profileData, error = null)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Failed to load profile data source")
                }
            }
        }
    }


    fun onShowChangePasswordModal() {
        _state.update { it.copy(showChangePasswordModal = true) }
    }

    fun onDismissChangePasswordModal() {
        _state.update {
            it.copy(
                showChangePasswordModal = false,
                passwordChangeError = null,
                passwordChangeSuccess = false
            )
        }
    }

    fun onChangePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.update { it.copy(isChangingPassword = true, passwordChangeError = null) }
            updatePasswordUseCase(currentPassword, newPassword).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            passwordChangeSuccess = true,
                            showChangePasswordModal = false
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            passwordChangeError = e.message ?: "Password change failed"
                        )
                    }
                }
            )
        }
    }

    fun onUpdateYearSemester(year: Int, semester: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingProfile = true) }
            updateNotificationPrefsUseCase(year, semester).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isUpdatingProfile = false,
                            showYearSemesterSheet = false,
                            successMessage = "Profile updated successfully"
                        )
                    }
                    loadMockProfile()

                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isUpdatingProfile = false,
                            error = e.message
                        )
                    }
                }
            )
        }
    }

    fun onShowYearSemesterSheet() {
        _state.update { it.copy(showYearSemesterSheet = true) }
    }

    fun onDismissYearSemesterSheet() {
        _state.update { it.copy(showYearSemesterSheet = false) }
    }

    fun onLogout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
        }
        onLoggedOut()
    }

    fun onClearSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }
}