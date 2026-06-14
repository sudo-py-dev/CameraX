package com.camerax.di

import android.content.Context
import com.camerax.data.local.AppDatabase
import com.camerax.data.local.PreferencesManager
import com.camerax.data.repository.MediaRepositoryImpl
import com.camerax.data.repository.PreferencesRepositoryImpl
import com.camerax.data.repository.ScanHistoryRepositoryImpl
import com.camerax.domain.repository.MediaRepository
import com.camerax.domain.repository.PreferencesRepository
import com.camerax.domain.repository.ScanHistoryRepository

interface AppContainer {
    val preferencesRepository: PreferencesRepository
    val mediaRepository: MediaRepository
    val scanHistoryRepository: ScanHistoryRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        AppDatabase.build(context)
    }

    private val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepositoryImpl(preferencesManager)
    }

    override val mediaRepository: MediaRepository by lazy {
        MediaRepositoryImpl(database.mediaDao)
    }

    override val scanHistoryRepository: ScanHistoryRepository by lazy {
        ScanHistoryRepositoryImpl(database.scanHistoryDao)
    }
}
