package com.example.shilpakala.domain.model

data class UserProfile(
    val id: String,
    val email: String,
    val artisanName: String,
    val workshopLocation: String,
    val profilePhotoUri: String?,
    val contact: String,
    val preferredLanguage: AppLanguage = AppLanguage.English,
    val preferredTheme: AppTheme = AppTheme.Light,
    val pendingSync: Boolean = false
)

data class PhotoMetadata(
    val artisanName: String,
    val productName: String,
    val woodType: String,
    val price: String,
    val backgroundTheme: BackgroundTheme,
    val overlayX: Float = 0.08f,
    val overlayY: Float = 0.78f
)

data class PortfolioPhoto(
    val id: String,
    val userId: String,
    val originalUri: String,
    val processedUri: String,
    val timestamp: Long,
    val heritageLabel: String,
    val metadata: PhotoMetadata,
    val sharesCount: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.Pending
)

enum class BackgroundTheme(val label: String) {
    Studio("Studio"),
    Wooden("Wooden"),
    Festival("Festival")
}

enum class AppLanguage {
    English,
    Kannada
}

enum class AppTheme {
    Light,
    Dark
}

enum class SyncStatus {
    Pending,
    Synced,
    Failed
}

enum class CameraControllerState {
    Idle,
    Previewing,
    Capturing,
    Processing,
    Completed,
    Error
}

data class AnalyticsSummary(
    val photosCreated: Int = 0,
    val sharesCount: Int = 0
)
