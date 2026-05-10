package com.unipapers.unipapers_frontend.feature.profile.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.domain.model.User
import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileResponse
import com.unipapers.unipapers_frontend.feature.profile.domain.model.UserInfo
import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileStats
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
    private val getProfileUseCase: GetProfileUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val updateNotificationPrefsUseCase: UpdateNotificationPrefsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = withContext(Dispatchers.IO) { getProfileUseCase() }
                result.fold(
                    onSuccess = { remoteUser ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                profile = remoteUser.toProfileResponse(),
                                error = null
                            )
                        }
                    },
                    onFailure = { e ->
                        val mockProfile = runCatching { loadMockProfileResponse() }
                            .getOrElse { null }
                        if (mockProfile != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    profile = mockProfile,
                                    error = null // Clear error if mock succeeds, or keep it as a warning
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = e.message ?: "Failed to load profile data source"
                                )
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load profile") }
            }
        }
    }

    private suspend fun loadMockProfileResponse(): ProfileResponse = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("profile_mock.json")
            .bufferedReader()
            .use { it.readText() }
        gson.fromJson(jsonString, ProfileResponse::class.java)
    }

    private fun User.toProfileResponse(): ProfileResponse = ProfileResponse(
        user = UserInfo(
            fullName = fullName,
            email = email,
            institution = "Your Institution", // TODO: Add institution to User domain model if needed
            studentId = studentNumber,
            programme = programme,
            yearOfStudy = yearOfStudy,
            currentSemester = currentSemester
        ),
        stats = ProfileStats(
            uploadedPastPapers = uploadCount
        ),
        settingsOptions = emptyList()
    )


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
                    loadProfile()

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