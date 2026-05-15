package com.example.shilpakala.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.shilpakala.data.local.MetadataDao
import com.example.shilpakala.data.local.PhotoDao
import com.example.shilpakala.data.local.ShilpaKalaDatabase
import com.example.shilpakala.data.local.UserDao
import com.example.shilpakala.utils.BackgroundRemovalEngine
import com.example.shilpakala.utils.HeuristicBackgroundRemovalEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShilpaKalaDatabase =
        Room.databaseBuilder(context, ShilpaKalaDatabase::class.java, "shilpakala.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserDao(database: ShilpaKalaDatabase): UserDao = database.userDao()
    @Provides fun providePhotoDao(database: ShilpaKalaDatabase): PhotoDao = database.photoDao()
    @Provides fun provideMetadataDao(database: ShilpaKalaDatabase): MetadataDao = database.metadataDao()

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("shilpakala_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideBackgroundRemovalEngine(
        engine: HeuristicBackgroundRemovalEngine
    ): BackgroundRemovalEngine = engine
}
