package com.example.shilpakala.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakala.data.repository.AuthRepository
import com.example.shilpakala.data.repository.PhotoRepository
import com.example.shilpakala.data.repository.SettingsRepository
import com.example.shilpakala.domain.model.AnalyticsSummary
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.example.shilpakala.domain.model.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val analytics: AnalyticsSummary = AnalyticsSummary(),
    val recentPhotos: List<PortfolioPhoto> = emptyList(),
    val reminderEnabled: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.Pending
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    photoRepository: PhotoRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        val userId = authRepository.currentUserId()
        if (userId != null) {
            viewModelScope.launch {
                combine(
                    photoRepository.observeAnalytics(userId),
                    photoRepository.observeRecentPhotos(userId),
                    settingsRepository.settings
                ) { analytics, recentPhotos, settings ->
                    HomeUiState(
                        analytics = analytics,
                        recentPhotos = recentPhotos,
                        reminderEnabled = settings.reminderEnabled,
                        syncStatus = recentPhotos.firstOrNull()?.syncStatus ?: SyncStatus.Pending
                    )
                }.collect { _state.value = it }
            }
        }
    }
}
