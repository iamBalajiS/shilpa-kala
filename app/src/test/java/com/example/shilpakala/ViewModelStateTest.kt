package com.example.shilpakala

import com.example.shilpakala.domain.model.AppLanguage
import com.example.shilpakala.domain.model.AppTheme
import com.example.shilpakala.domain.model.BackgroundTheme
import com.example.shilpakala.domain.model.CameraControllerState
import com.example.shilpakala.ui.camera.CameraUiState
import com.example.shilpakala.ui.settings.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ViewModelStateTest {
    @Test
    fun cameraStateDefaultsToStudioTheme() {
        val state = CameraUiState()
        assertEquals(BackgroundTheme.Studio, state.backgroundTheme)
        assertFalse(state.isProcessing)
        assertEquals(CameraControllerState.Idle, state.controllerState)
    }

    @Test
    fun settingsStateStartsAccessibleAndOfflineFriendly() {
        val state = SettingsUiState()
        assertEquals(AppTheme.Light, state.theme)
        assertEquals(AppLanguage.English, state.language)
        assertFalse(state.reminderEnabled)
    }
}
