package com.unipapers.unipapers_frontend.feature.profile.presentation
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase [cite: 156]
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Invoke the use case
            val result = getProfileUseCase()

            // Handle the result (assuming a Result wrapper)
            result.onSuccess { userData ->
                _state.update { it.copy(user = userData, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }
}