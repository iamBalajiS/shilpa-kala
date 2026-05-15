package com.example.shilpakala.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakala.data.repository.PhotoRepository
import com.example.shilpakala.data.repository.SettingsRepository
import com.example.shilpakala.domain.model.AppLanguage
import com.example.shilpakala.domain.model.AppTheme
import com.example.shilpakala.utils.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val theme: AppTheme = AppTheme.Light,
    val language: AppLanguage = AppLanguage.English,
    val reminderEnabled: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
    private val settingsRepository: SettingsRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _state.value = SettingsUiState(
                    theme = settings.theme,
                    language = settings.language,
                    reminderEnabled = settings.reminderEnabled
                )
            }
        }
    }

    fun setTheme(theme: AppTheme) = settingsRepository.updateTheme(theme)
    fun setLanguage(language: AppLanguage) = settingsRepository.updateLanguage(language)

    fun setReminder(enabled: Boolean) {
        settingsRepository.updateReminder(enabled)
        if (enabled) reminderScheduler.scheduleDailyReminder()
    }

    fun syncNow() {
        viewModelScope.launch { photoRepository.syncPendingPhotos() }
    }
}
