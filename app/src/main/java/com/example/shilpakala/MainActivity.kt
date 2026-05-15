package com.example.shilpakala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shilpakala.domain.model.AppTheme
import com.example.shilpakala.ui.navigation.ShilpaKalaApp
import com.example.shilpakala.ui.settings.SettingsViewModel
import com.example.shilpakala.ui.theme.ShilpaKalaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShilpaKalaRoot()
        }
    }
}

@Composable
private fun ShilpaKalaRoot(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val settings = settingsViewModel.state.collectAsStateWithLifecycle().value
    ShilpaKalaTheme(darkTheme = settings.theme == AppTheme.Dark) {
        ShilpaKalaApp()
    }
}
