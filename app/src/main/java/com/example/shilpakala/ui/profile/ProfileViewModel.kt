package com.example.shilpakala.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakala.data.repository.ProfileRepository
import com.example.shilpakala.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: UserProfile? = null,
    val artisanName: String = "",
    val workshopLocation: String = "",
    val contact: String = "",
    val profilePhotoUri: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.observeProfile().collect { profile ->
                _state.value = ProfileUiState(
                    profile = profile,
                    artisanName = profile?.artisanName.orEmpty(),
                    workshopLocation = profile?.workshopLocation.orEmpty(),
                    contact = profile?.contact.orEmpty(),
                    profilePhotoUri = profile?.profilePhotoUri
                )
            }
        }
    }

    fun updateName(value: String) = _state.update { it.copy(artisanName = value) }
    fun updateLocation(value: String) = _state.update { it.copy(workshopLocation = value) }
    fun updateContact(value: String) = _state.update { it.copy(contact = value) }
    fun updatePhotoUri(value: String?) = _state.update { it.copy(profilePhotoUri = value) }

    fun save() {
        val snapshot = state.value
        val profile = snapshot.profile ?: return
        viewModelScope.launch {
            profileRepository.updateProfile(
                profile.copy(
                    artisanName = snapshot.artisanName,
                    workshopLocation = snapshot.workshopLocation,
                    contact = snapshot.contact,
                    profilePhotoUri = snapshot.profilePhotoUri
                )
            )
        }
    }
}
