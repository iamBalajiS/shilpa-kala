package com.example.shilpakala.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable
fun HomeScreen(
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleReminder: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.home_subtitle))
        }
        item {
            Button(onClick = onOpenCamera, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Text("  ${stringResource(R.string.capture_now)}")
            }
        }
        item {
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard(stringResource(R.string.capture_now), Icons.Default.CameraAlt, onOpenCamera)
                    ActionCard(stringResource(R.string.open_gallery), Icons.Default.ViewModule, onOpenGallery)
                    ActionCard(stringResource(R.string.edit_profile), Icons.Default.Person, onOpenProfile)
                    ActionCard(stringResource(R.string.settings), Icons.Default.Settings, onOpenSettings)
                    ActionCard(stringResource(R.string.daily_reminder), Icons.Default.Notifications, onToggleReminder)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(stringResource(R.string.photos_created), state.analytics.photosCreated.toString(), Modifier.weight(1f))
                StatCard(stringResource(R.string.shares_count), state.analytics.sharesCount.toString(), Modifier.weight(1f))
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Sync, contentDescription = null)
                    Column {
                        Text(stringResource(R.string.sync_status), fontWeight = FontWeight.Bold)
                        Text(state.syncStatus.name)
                    }
                }
            }
        }
        item {
            Text(stringResource(R.string.recent_photos), style = MaterialTheme.typography.titleLarge)
        }
        items(state.recentPhotos, key = { it.id }) { photo ->
            Card(Modifier.fillMaxWidth().clickable(onClick = onOpenGallery)) {
                Column(Modifier.padding(16.dp)) {
                    Text(photo.metadata.productName, fontWeight = FontWeight.Bold)
                    Text(photo.heritageLabel, maxLines = 2)
                }
            }
        }
    }
}

@Composable
private fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null)
            Text(title)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label)
        }
    }
}
