package com.example.shilpakala.domain.usecase

import android.net.Uri
import com.example.shilpakala.data.repository.AuthRepository
import com.example.shilpakala.data.repository.PhotoRepository
import com.example.shilpakala.domain.model.PhotoMetadata
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.example.shilpakala.utils.BackgroundRemovalEngine
import com.example.shilpakala.utils.HeritageLabelGenerator
import com.example.shilpakala.utils.ImageProcessor
import javax.inject.Inject

class PhotoProcessingUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotoRepository,
    private val imageProcessor: ImageProcessor,
    private val backgroundRemovalEngine: BackgroundRemovalEngine,
    private val heritageLabelGenerator: HeritageLabelGenerator
) {
    suspend operator fun invoke(sourceUri: Uri, metadata: PhotoMetadata): PortfolioPhoto {
        val userId = authRepository.currentUserId() ?: error("User session missing")
        val processed = imageProcessor.process(sourceUri, metadata, backgroundRemovalEngine)
        val label = heritageLabelGenerator.generate(metadata)
        return photoRepository.savePhoto(
            userId = userId,
            originalUri = processed.originalUri.toString(),
            processedUri = processed.processedUri.toString(),
            metadata = metadata,
            heritageLabel = label
        )
    }
}
