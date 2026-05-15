package com.example.shilpakala.data.repository

import com.example.shilpakala.data.local.UserDao
import com.example.shilpakala.domain.model.UserProfile
import com.example.shilpakala.utils.CloudSyncService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val userDao: UserDao,
    private val authRepository: AuthRepository,
    private val cloudSyncService: CloudSyncService
) {
    fun observeProfile(): Flow<UserProfile?> {
        val id = authRepository.currentUserId() ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.observeUser(id).map { it?.toProfile() }
    }

    suspend fun updateProfile(profile: UserProfile) {
        val existing = userDao.getUser(profile.id)
        val updated = profile.copy(pendingSync = true)
        userDao.upsert(updated.toEntity(existing?.passwordHash.orEmpty()))
        val synced = cloudSyncService.syncProfile(updated)
        if (synced) {
            userDao.upsert(updated.copy(pendingSync = false).toEntity(existing?.passwordHash.orEmpty()))
        }
    }
}
