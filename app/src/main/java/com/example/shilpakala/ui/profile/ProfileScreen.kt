package com.example.shilpakala.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.shilpakala.R
import com.example.shilpakala.utils.QrCodeGenerator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val qr = remember(state.contact) { QrCodeGenerator().generate("Contact ${state.artisanName}: ${state.contact}") }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.updatePhotoUri(uri?.toString())
    }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(stringResource(R.string.profile), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        AsyncImage(
            model = state.profilePhotoUri,
            contentDescription = stringResource(R.string.profile_photo),
            modifier = Modifier.size(120.dp).clip(CircleShape)
        )
        Button(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.choose_profile_photo))
        }
        OutlinedTextField(state.artisanName, viewModel::updateName, label = { Text(stringResource(R.string.artisan_name)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.workshopLocation, viewModel::updateLocation, label = { Text(stringResource(R.string.workshop_location)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.contact, viewModel::updateContact, label = { Text(stringResource(R.string.contact)) }, modifier = Modifier.fillMaxWidth())
        Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.save_profile)) }
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                androidx.compose.foundation.Image(qr.asImageBitmap(), contentDescription = stringResource(R.string.contact_qr))
                Column {
                    Text(stringResource(R.string.contact_qr), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.contact_qr_description))
                }
            }
        }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.logout)) }
    }
}
