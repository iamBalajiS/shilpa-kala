package com.example.shilpakala.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.shilpakala.domain.model.AppLanguage
import com.example.shilpakala.domain.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class SettingsSnapshot(
    val theme: AppTheme = AppTheme.Light,
    val language: AppLanguage = AppLanguage.English,
    val reminderEnabled: Boolean = false
)

@Singleton
class SettingsRepository @Inject constructor(
    private val preferences: SharedPreferences
) {
    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<SettingsSnapshot> = _settings.asStateFlow()

    fun updateTheme(theme: AppTheme) {
        preferences.edit().putString(KEY_THEME, theme.name).apply()
        _settings.value = readSettings()
    }

    fun updateLanguage(language: AppLanguage) {
        preferences.edit().putString(KEY_LANGUAGE, language.name).apply()
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(if (language == AppLanguage.Kannada) "kn" else "en")
        )
        _settings.value = readSettings()
    }

    fun updateReminder(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_REMINDER, enabled).apply()
        _settings.value = readSettings()
    }

    fun applySavedLanguage() {
        val language = readSettings().language
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(if (language == AppLanguage.Kannada) "kn" else "en")
        )
    }

    private fun readSettings(): SettingsSnapshot = SettingsSnapshot(
        theme = runCatching {
            AppTheme.valueOf(preferences.getString(KEY_THEME, AppTheme.Light.name).orEmpty())
        }.getOrDefault(AppTheme.Light),
        language = runCatching {
            AppLanguage.valueOf(preferences.getString(KEY_LANGUAGE, AppLanguage.English.name).orEmpty())
        }.getOrDefault(AppLanguage.English),
        reminderEnabled = preferences.getBoolean(KEY_REMINDER, false)
    )

    private companion object {
        const val KEY_THEME = "pref_theme"
        const val KEY_LANGUAGE = "pref_language"
        const val KEY_REMINDER = "pref_reminder"
    }
}
