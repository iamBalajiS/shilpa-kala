package com.example.shilpakala.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, PhotoEntity::class, MetadataEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ShilpaKalaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun photoDao(): PhotoDao
    abstract fun metadataDao(): MetadataDao
}
