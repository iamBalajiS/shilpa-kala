package com.example.shilpakala.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUser(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE userId = :userId ORDER BY timestamp DESC")
    fun observePhotos(userId: String): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE userId = :userId ORDER BY timestamp DESC LIMIT 3")
    fun observeRecentPhotos(userId: String): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhoto(photoId: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE syncStatus != 'Synced'")
    suspend fun getPendingPhotos(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(photo: PhotoEntity)

    @Query("UPDATE photos SET sharesCount = sharesCount + 1 WHERE id = :photoId")
    suspend fun incrementShare(photoId: String)

    @Query("UPDATE photos SET syncStatus = :status WHERE id = :photoId")
    suspend fun updateSyncStatus(photoId: String, status: String)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun delete(photoId: String)
}

@Dao
interface MetadataDao {
    @Query("SELECT * FROM metadata WHERE photoId = :photoId LIMIT 1")
    suspend fun getForPhoto(photoId: String): MetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: MetadataEntity)

    @Query("DELETE FROM metadata WHERE photoId = :photoId")
    suspend fun delete(photoId: String)
}
