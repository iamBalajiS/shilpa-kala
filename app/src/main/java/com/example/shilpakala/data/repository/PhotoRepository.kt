package com.example.shilpakala.data.repository

import com.example.shilpakala.data.local.MetadataDao
import com.example.shilpakala.data.local.PhotoDao
import com.example.shilpakala.data.local.PhotoEntity
import com.example.shilpakala.domain.model.AnalyticsSummary
import com.example.shilpakala.domain.model.PhotoMetadata
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.example.shilpakala.domain.model.SyncStatus
import com.example.shilpakala.utils.CloudSyncService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val photoDao: PhotoDao,
    private val metadataDao: MetadataDao,
    private val cloudSyncService: CloudSyncService
) {
    fun observePhotos(userId: String): Flow<List<PortfolioPhoto>> =
        photoDao.observePhotos(userId).map { photos ->
            photos.mapNotNull { photo ->
                val meta = metadataDao.getForPhoto(photo.id)?.toMetadata() ?: return@mapNotNull null
                photo.toPortfolioPhoto(meta)
            }
        }

    fun observeRecentPhotos(userId: String): Flow<List<PortfolioPhoto>> =
        photoDao.observeRecentPhotos(userId).map { photos ->
            photos.mapNotNull { photo ->
                val meta = metadataDao.getForPhoto(photo.id)?.toMetadata() ?: return@mapNotNull null
                photo.toPortfolioPhoto(meta)
            }
        }

    fun observeAnalytics(userId: String): Flow<AnalyticsSummary> =
        observePhotos(userId).map { photos ->
            AnalyticsSummary(
                photosCreated = photos.size,
                sharesCount = photos.sumOf { it.sharesCount }
            )
        }

    suspend fun savePhoto(
        userId: String,
        originalUri: String,
        processedUri: String,
        metadata: PhotoMetadata,
        heritageLabel: String
    ): PortfolioPhoto {
        val id = UUID.randomUUID().toString()
        val photo = PhotoEntity(
            id = id,
            userId = userId,
            originalUri = originalUri,
            processedUri = processedUri,
            timestamp = System.currentTimeMillis(),
            heritageLabel = heritageLabel,
            sharesCount = 0,
            syncStatus = SyncStatus.Pending.name
        )
        photoDao.upsert(photo)
        metadataDao.upsert(metadata.toEntity(id))
        val pendingPhoto = photo.toPortfolioPhoto(metadata)
        val synced = cloudSyncService.syncPhoto(pendingPhoto)
        val finalStatus = if (synced) SyncStatus.Synced else SyncStatus.Failed
        photoDao.updateSyncStatus(id, finalStatus.name)
        return pendingPhoto.copy(syncStatus = finalStatus)
    }

    suspend fun incrementShare(photoId: String) {
        photoDao.incrementShare(photoId)
    }

    suspend fun delete(photoId: String) {
        metadataDao.delete(photoId)
        photoDao.delete(photoId)
        cloudSyncService.deletePhoto(photoId)
    }

    suspend fun syncPendingPhotos() {
        photoDao.getPendingPhotos().forEach { photo ->
            val metadata = metadataDao.getForPhoto(photo.id)?.toMetadata() ?: return@forEach
            val synced = cloudSyncService.syncPhoto(photo.toPortfolioPhoto(metadata))
            photoDao.updateSyncStatus(photo.id, if (synced) SyncStatus.Synced.name else SyncStatus.Failed.name)
        }
    }
}
