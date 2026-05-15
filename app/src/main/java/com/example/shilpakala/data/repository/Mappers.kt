package com.example.shilpakala.data.repository

import com.example.shilpakala.data.local.MetadataEntity
import com.example.shilpakala.data.local.PhotoEntity
import com.example.shilpakala.data.local.UserEntity
import com.example.shilpakala.domain.model.AppLanguage
import com.example.shilpakala.domain.model.AppTheme
import com.example.shilpakala.domain.model.BackgroundTheme
import com.example.shilpakala.domain.model.PhotoMetadata
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.example.shilpakala.domain.model.SyncStatus
import com.example.shilpakala.domain.model.UserProfile

fun UserEntity.toProfile() = UserProfile(
    id = id,
    email = email,
    artisanName = artisanName,
    workshopLocation = workshopLocation,
    profilePhotoUri = profilePhotoUri,
    contact = contact,
    preferredLanguage = runCatching { AppLanguage.valueOf(preferredLanguage) }.getOrDefault(AppLanguage.English),
    preferredTheme = runCatching { AppTheme.valueOf(preferredTheme) }.getOrDefault(AppTheme.Light),
    pendingSync = pendingSync
)

fun UserProfile.toEntity(passwordHash: String = "") = UserEntity(
    id = id,
    email = email,
    passwordHash = passwordHash,
    artisanName = artisanName,
    workshopLocation = workshopLocation,
    profilePhotoUri = profilePhotoUri,
    contact = contact,
    preferredLanguage = preferredLanguage.name,
    preferredTheme = preferredTheme.name,
    pendingSync = pendingSync
)

fun MetadataEntity.toMetadata() = PhotoMetadata(
    artisanName = artisanName,
    productName = productName,
    woodType = woodType,
    price = price,
    backgroundTheme = runCatching { BackgroundTheme.valueOf(backgroundTheme) }.getOrDefault(BackgroundTheme.Studio),
    overlayX = overlayX,
    overlayY = overlayY
)

fun PhotoMetadata.toEntity(photoId: String) = MetadataEntity(
    photoId = photoId,
    artisanName = artisanName,
    productName = productName,
    woodType = woodType,
    price = price,
    backgroundTheme = backgroundTheme.name,
    overlayX = overlayX,
    overlayY = overlayY
)

fun PhotoEntity.toPortfolioPhoto(metadata: PhotoMetadata) = PortfolioPhoto(
    id = id,
    userId = userId,
    originalUri = originalUri,
    processedUri = processedUri,
    timestamp = timestamp,
    heritageLabel = heritageLabel,
    metadata = metadata,
    sharesCount = sharesCount,
    syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.Pending)
)
