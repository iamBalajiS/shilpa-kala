package com.example.shilpakala.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shilpakala.R
import com.example.shilpakala.domain.model.AppLanguage
import com.example.shilpakala.domain.model.AppTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.theme))
                ThemeOption(stringResource(R.string.light_theme), state.theme == AppTheme.Light) { viewModel.setTheme(AppTheme.Light) }
                ThemeOption(stringResource(R.string.dark_theme), state.theme == AppTheme.Dark) { viewModel.setTheme(AppTheme.Dark) }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.language))
                ThemeOption(stringResource(R.string.english), state.language == AppLanguage.English) { viewModel.setLanguage(AppLanguage.English) }
                ThemeOption(stringResource(R.string.kannada), state.language == AppLanguage.Kannada) { viewModel.setLanguage(AppLanguage.Kannada) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.daily_reminder))
            Switch(checked = state.reminderEnabled, onCheckedChange = { viewModel.setReminder(it) })
        }
        Button(onClick = viewModel::syncNow, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.sync_now))
        }
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        RadioButton(selected = selected, onClick = onClick)
    }
}
