package com.example.shilpakala

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.shilpakala.data.repository.SettingsRepository
import com.example.shilpakala.utils.ReminderScheduler
import com.example.shilpakala.utils.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ShilpaKalaApplication : Application(), Configuration.Provider {
    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        settingsRepository.applySavedLanguage()
        reminderScheduler.createNotificationChannel()
        SyncWorker.enqueue(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
