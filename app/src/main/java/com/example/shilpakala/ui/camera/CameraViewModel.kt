package com.example.shilpakala.ui.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakala.domain.model.BackgroundTheme
import com.example.shilpakala.domain.model.CameraControllerState
import com.example.shilpakala.domain.model.PhotoMetadata
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.example.shilpakala.domain.usecase.PhotoProcessingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraUiState(
    val artisanName: String = "",
    val productName: String = "",
    val woodType: String = "",
    val price: String = "",
    val backgroundTheme: BackgroundTheme = BackgroundTheme.Studio,
    val overlayX: Float = 0.08f,
    val overlayY: Float = 0.78f,
    val controllerState: CameraControllerState = CameraControllerState.Idle,
    val previewHint: String = "",
    val averageLuma: Double = 0.0,
    val isProcessing: Boolean = false,
    val lastPhoto: PortfolioPhoto? = null,
    val originalUri: String? = null,
    val processedUri: String? = null,
    val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val processPhoto: PhotoProcessingUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CameraUiState())
    val state: StateFlow<CameraUiState> = _state.asStateFlow()

    fun updateArtisan(value: String) = _state.update { it.copy(artisanName = value) }
    fun updateProduct(value: String) = _state.update { it.copy(productName = value) }
    fun updateWood(value: String) = _state.update { it.copy(woodType = value) }
    fun updatePrice(value: String) = _state.update { it.copy(price = value) }
    fun updateTheme(value: BackgroundTheme) = _state.update { it.copy(backgroundTheme = value) }
    fun updateOverlayX(value: Float) = _state.update { it.copy(overlayX = value) }
    fun updateOverlayY(value: Float) = _state.update { it.copy(overlayY = value) }
    fun onPreviewStarted() = _state.update { it.copy(controllerState = CameraControllerState.Previewing) }
    fun onCaptureStarted() = _state.update { it.copy(controllerState = CameraControllerState.Capturing, isProcessing = false, error = null) }
    fun onFrameAnalyzed(luma: Double) {
        val hint = when {
            luma < 45 -> "Add more light"
            luma > 185 -> "Reduce glare"
            else -> "Lighting looks good"
        }
        _state.update { it.copy(averageLuma = luma, previewHint = hint) }
    }

    fun processCapturedImage(uri: Uri) {
        val snapshot = state.value
        val metadata = PhotoMetadata(
            artisanName = snapshot.artisanName.ifBlank { "Local Artisan" },
            productName = snapshot.productName.ifBlank { "Wood Craft" },
            woodType = snapshot.woodType.ifBlank { "Natural Wood" },
            price = snapshot.price.ifBlank { "On request" },
            backgroundTheme = snapshot.backgroundTheme,
            overlayX = snapshot.overlayX,
            overlayY = snapshot.overlayY
        )
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, controllerState = CameraControllerState.Processing, error = null) }
            runCatching {
                processPhoto(uri, metadata)
            }.onSuccess { photo ->
                _state.update {
                    it.copy(
                        isProcessing = false,
                        controllerState = CameraControllerState.Completed,
                        lastPhoto = photo,
                        originalUri = photo.originalUri,
                        processedUri = photo.processedUri
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isProcessing = false,
                        controllerState = CameraControllerState.Error,
                        error = throwable.message ?: "Image processing failed"
                    )
                }
            }
        }
    }
}
