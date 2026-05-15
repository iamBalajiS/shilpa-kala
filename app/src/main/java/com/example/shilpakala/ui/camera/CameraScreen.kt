package com.example.shilpakala.ui.camera

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.shilpakala.R
import com.example.shilpakala.domain.model.BackgroundTheme
import com.example.shilpakala.domain.model.CameraControllerState
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> hasPermission = granted }
    LaunchedEffect(Unit) { if (!hasPermission) launcher.launch(Manifest.permission.CAMERA) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (hasPermission) {
            CameraPreview(
                modifier = Modifier.fillMaxWidth().height(430.dp),
                onImageCaptured = viewModel::processCapturedImage,
                onPreviewStarted = viewModel::onPreviewStarted,
                onCaptureStarted = viewModel::onCaptureStarted,
                onFrameAnalyzed = viewModel::onFrameAnalyzed
            )
        } else {
            Box(Modifier.fillMaxWidth().height(430.dp), contentAlignment = Alignment.Center) {
                Text("Camera permission is required")
            }
        }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Status: ${state.controllerState.name}")
                    Text("Live hint: ${state.previewHint}")
                    Text("Theme preview: ${state.backgroundTheme.label}")
                }
            }
            OutlinedTextField(state.artisanName, viewModel::updateArtisan, label = { Text("Artisan name") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(state.productName, viewModel::updateProduct, label = { Text("Product") }, modifier = Modifier.weight(1f))
                OutlinedTextField(state.woodType, viewModel::updateWood, label = { Text("Wood type") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(state.price, viewModel::updatePrice, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
            Text("Background")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BackgroundTheme.entries.forEach { theme ->
                    FilterChip(selected = state.backgroundTheme == theme, onClick = { viewModel.updateTheme(theme) }, label = { Text(theme.label) })
                }
            }
            Text("Overlay position")
            Slider(state.overlayX, viewModel::updateOverlayX, valueRange = 0f..0.2f)
            Slider(state.overlayY, viewModel::updateOverlayY, valueRange = 0.6f..0.9f)
            AnimatedVisibility(state.isProcessing) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Text("  Enhancing photo and generating heritage label...")
                }
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (state.originalUri != null && state.processedUri != null) {
                BeforeAfterView(state.originalUri!!, state.processedUri!!)
                Text(state.lastPhoto?.heritageLabel.orEmpty(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier,
    onImageCaptured: (Uri) -> Unit,
    onPreviewStarted: () -> Unit = {},
    onCaptureStarted: () -> Unit = {},
    onFrameAnalyzed: (Double) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build() }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) { onDispose { executor.shutdown() } }

    Box(modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                        imageAnalysis.setAnalyzer(executor) { proxy ->
                            val buffer = proxy.planes.firstOrNull()?.buffer
                            if (buffer != null) {
                                var sum = 0L
                                val size = buffer.remaining()
                                val step = (size / 2000).coerceAtLeast(1)
                                for (index in 0 until size step step) {
                                    sum += buffer.get(index).toInt() and 0xFF
                                }
                                val samples = (size / step).coerceAtLeast(1)
                                onFrameAnalyzed(sum.toDouble() / samples.toDouble())
                            }
                            proxy.close()
                        }
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, imageAnalysis)
                        onPreviewStarted()
                    }, ContextCompat.getMainExecutor(ctx))
                }
            }
        )
        GuideOverlay(Modifier.fillMaxSize())
        Button(
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp).fillMaxWidth(0.82f).height(56.dp),
            onClick = {
                onCaptureStarted()
                val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
                val options = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        onImageCaptured(Uri.fromFile(file))
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraScreen", "Capture failed", exception)
                    }
                })
            }
        ) { Text("Capture Product Photo") }
    }
}

@Composable
private fun GuideOverlay(modifier: Modifier) {
    Canvas(modifier) {
        val frameWidth = size.width * 0.68f
        val frameHeight = size.height * 0.52f
        val left = (size.width - frameWidth) / 2f
        val top = size.height * 0.16f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(frameWidth, frameHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(42f, 42f),
            style = Stroke(width = 5f)
        )
        drawLine(Color.White.copy(alpha = 0.45f), Offset(size.width / 3f, 0f), Offset(size.width / 3f, size.height), 2f)
        drawLine(Color.White.copy(alpha = 0.45f), Offset(size.width * 2f / 3f, 0f), Offset(size.width * 2f / 3f, size.height), 2f)
        drawLine(Color.White.copy(alpha = 0.45f), Offset(0f, size.height / 3f), Offset(size.width, size.height / 3f), 2f)
        drawLine(Color.White.copy(alpha = 0.45f), Offset(0f, size.height * 2f / 3f), Offset(size.width, size.height * 2f / 3f), 2f)
    }
}

@Composable
private fun BeforeAfterView(originalUri: String, processedUri: String) {
    var slider by remember { mutableStateOf(0.5f) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Before / After")
        Box(Modifier.fillMaxWidth().height(260.dp)) {
            AsyncImage(processedUri, contentDescription = "Processed", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            AsyncImage(originalUri, contentDescription = "Original", modifier = Modifier.fillMaxWidth(slider).height(260.dp), contentScale = ContentScale.Crop)
        }
        Slider(value = slider, onValueChange = { slider = it })
    }
}
