package com.example.shilpakala.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakala.data.repository.AuthRepository
import com.example.shilpakala.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val user: UserProfile? = null,
    val isLogin: Boolean = true,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    fun updateEmail(value: String) = _state.update { it.copy(email = value, error = null) }
    fun updatePassword(value: String) = _state.update { it.copy(password = value, error = null) }
    fun updateConfirmPassword(value: String) = _state.update { it.copy(confirmPassword = value, error = null) }
    fun toggleMode() = _state.update { it.copy(isLogin = !it.isLogin, error = null) }

    fun submit() {
        val snapshot = state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = if (snapshot.isLogin) {
                authRepository.login(snapshot.email, snapshot.password)
            } else {
                authRepository.signUp(snapshot.email, snapshot.password, snapshot.confirmPassword)
            }
            _state.update { it.copy(isLoading = false, user = result.getOrNull(), error = result.exceptionOrNull()?.message) }
        }
    }

    fun signInWithGoogle(email: String?, displayName: String?, idToken: String?) {
        if (email.isNullOrBlank()) {
            _state.update { it.copy(error = "Google account has no email") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.googleLogin(email, displayName, idToken)
            _state.update { it.copy(isLoading = false, user = result.getOrNull(), error = result.exceptionOrNull()?.message) }
        }
    }

    fun logout() {
        authRepository.logout()
        _state.value = AuthUiState()
    }
}
