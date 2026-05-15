package com.example.shilpakala.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val passwordHash: String,
    val artisanName: String,
    val workshopLocation: String,
    val profilePhotoUri: String?,
    val contact: String,
    val preferredLanguage: String,
    val preferredTheme: String,
    val pendingSync: Boolean
)

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val originalUri: String,
    val processedUri: String,
    val timestamp: Long,
    val heritageLabel: String,
    val sharesCount: Int,
    val syncStatus: String
)

@Entity(tableName = "metadata")
data class MetadataEntity(
    @PrimaryKey val photoId: String,
    val artisanName: String,
    val productName: String,
    val woodType: String,
    val price: String,
    val backgroundTheme: String,
    val overlayX: Float,
    val overlayY: Float
)
