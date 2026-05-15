package com.example.shilpakala.data.repository

import android.content.SharedPreferences
import com.example.shilpakala.data.local.UserDao
import com.example.shilpakala.data.local.UserEntity
import com.example.shilpakala.domain.model.AppLanguage
import com.example.shilpakala.domain.model.AppTheme
import com.example.shilpakala.domain.model.UserProfile
import com.example.shilpakala.utils.CloudSyncService
import com.example.shilpakala.utils.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val preferences: SharedPreferences,
    private val passwordHasher: PasswordHasher,
    private val cloudSyncService: CloudSyncService
) {
    fun currentUserId(): String? = cloudSyncService.currentFirebaseUserId() ?: preferences.getString(KEY_USER_ID, null)

    fun observeCurrentUser(): Flow<UserProfile?> {
        val id = currentUserId() ?: return flowOf(null)
        return userDao.observeUser(id).map { it?.toProfile() }
    }

    suspend fun signUp(email: String, password: String, confirmPassword: String): Result<UserProfile> {
        if (!email.contains("@")) return Result.failure(IllegalArgumentException("Enter a valid email address"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        if (password != confirmPassword) return Result.failure(IllegalArgumentException("Passwords do not match"))
        if (userDao.getUserByEmail(email.trim().lowercase()) != null) {
            return Result.failure(IllegalArgumentException("Account already exists"))
        }

        val firebaseId = cloudSyncService.signUpWithFirebase(email.trim().lowercase(), password)
        val entity = UserEntity(
            id = firebaseId ?: UUID.randomUUID().toString(),
            email = email.trim().lowercase(),
            passwordHash = passwordHasher.hash(password),
            artisanName = "Artisan",
            workshopLocation = "Karnataka",
            profilePhotoUri = null,
            contact = email.trim().lowercase(),
            preferredLanguage = AppLanguage.English.name,
            preferredTheme = AppTheme.Light.name,
            pendingSync = firebaseId == null
        )
        userDao.upsert(entity)
        preferences.edit().putString(KEY_USER_ID, entity.id).apply()
        cloudSyncService.syncProfile(entity.toProfile())
        return Result.success(entity.toProfile())
    }

    suspend fun login(email: String, password: String): Result<UserProfile> {
        val normalizedEmail = email.trim().lowercase()
        val firebaseId = cloudSyncService.loginWithFirebase(normalizedEmail, password)
        val localUser = userDao.getUserByEmail(normalizedEmail)
        if (firebaseId == null && localUser == null) {
            return Result.failure(IllegalArgumentException("Account not found"))
        }
        val user = localUser?.takeIf { it.passwordHash == passwordHasher.hash(password) || firebaseId != null }
            ?: return Result.failure(IllegalArgumentException("Incorrect password"))
        preferences.edit().putString(KEY_USER_ID, user.id).apply()
        return Result.success(user.toProfile())
    }

    suspend fun googleLogin(email: String, displayName: String?, idToken: String?): Result<UserProfile> {
        val normalizedEmail = email.trim().lowercase()
        val firebaseId = idToken?.let { cloudSyncService.loginWithGoogleIdToken(it) }
        val existing = userDao.getUserByEmail(normalizedEmail)
        val entity = existing ?: UserEntity(
            id = firebaseId ?: UUID.randomUUID().toString(),
            email = normalizedEmail,
            passwordHash = "GOOGLE_SIGN_IN",
            artisanName = displayName?.takeIf { it.isNotBlank() } ?: "Google Artisan",
            workshopLocation = "Karnataka",
            profilePhotoUri = null,
            contact = normalizedEmail,
            preferredLanguage = AppLanguage.English.name,
            preferredTheme = AppTheme.Light.name,
            pendingSync = firebaseId == null
        ).also { userDao.upsert(it) }
        preferences.edit().putString(KEY_USER_ID, entity.id).apply()
        cloudSyncService.syncProfile(entity.toProfile())
        return Result.success(entity.toProfile())
    }

    fun logout() {
        preferences.edit().remove(KEY_USER_ID).apply()
        cloudSyncService.logoutFirebase()
    }

    private companion object {
        const val KEY_USER_ID = "current_user_id"
    }
}
