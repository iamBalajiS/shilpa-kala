package com.example.shilpakala.ui.gallery

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.example.shilpakala.utils.ShareManager

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    shareManager: ShareManager = ShareManager()
) {
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selected by remember { mutableStateOf<PortfolioPhoto?>(null) }

    if (photos.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp)) {
            Text("No photos yet. Capture your first craft photo from the Camera tab.")
        }
        return
    }

    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = Modifier.fillMaxSize().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(photos, key = { it.id }) { photo ->
            Card(Modifier.clickable { selected = photo }) {
                Column {
                    AsyncImage(photo.processedUri, contentDescription = photo.metadata.productName, modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Crop)
                    Text(photo.metadata.productName, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    selected?.let { photo ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(photo.metadata.productName) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(photo.processedUri, contentDescription = null, modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Crop)
                    Text(photo.heritageLabel)
                    Row {
                        IconButton(onClick = {
                            shareManager.shareImage(context, Uri.parse(photo.processedUri))
                            viewModel.recordShare(photo.id)
                        }) { Icon(Icons.Default.Share, contentDescription = "Share") }
                        IconButton(onClick = {
                            viewModel.delete(photo.id)
                            selected = null
                        }) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("Close") } }
        )
    }
}
