package com.unipapers.unipapers_frontend.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipapers.unipapers_frontend.core.domain.model.User
import com.unipapers.unipapers_frontend.feature.profile.domain.usecase.GetProfileUseCase
import com.unipapers.unipapers_frontend.feature.profile.domain.usecase.UpdateNotificationPrefsUseCase
import com.unipapers.unipapers_frontend.feature.profile.domain.usecase.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
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
        _state.update {
            it.copy(
                isLoading = false,
                user = User(
                    id = "1",
                    fullName = "Gloria Nabukalu",
                    email = "ria.kalu@students.mak.ac.ug",
                    studentNumber = "22/U/1234",
                    programme = "BSc Software Engineering",
                    yearOfStudy = 2,
                    currentSemester = 2,
                    freeViewsRemaining = 2,
                    hasUnlockedAccess = false,
                    uploadCount = 0,
                    downloadCount = 1
                )
            )
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProfileUseCase().fold(
                onSuccess = { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
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
        onLoggedOut()
    }

    fun onClearSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }
}