package com.camerax.domain.repository

import com.camerax.domain.model.AspectRatioMode
import com.camerax.domain.model.FlashMode
import com.camerax.domain.model.Language
import com.camerax.domain.model.PhotoResolution
import com.camerax.domain.model.ThemeMode
import com.camerax.domain.model.TimerDelay
import com.camerax.domain.model.VideoQuality
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val themeMode: Flow<ThemeMode>
    val language: Flow<Language>
    val gridEnabled: Flow<Boolean>
    val shutterSoundEnabled: Flow<Boolean>

    val flashMode: Flow<FlashMode>
    val timerDelay: Flow<TimerDelay>
    val aspectRatio: Flow<AspectRatioMode>
    val photoResolution: Flow<PhotoResolution>
    val videoQuality: Flow<VideoQuality>
    val hdrEnabled: Flow<Boolean>
    val stabilizationEnabled: Flow<Boolean>
    val autoCopyScans: Flow<Boolean>
    val vibrateOnScan: Flow<Boolean>
    val beepOnScan: Flow<Boolean>

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setLanguage(language: Language)

    suspend fun setGridEnabled(enabled: Boolean)

    suspend fun setShutterSoundEnabled(enabled: Boolean)

    suspend fun setFlashMode(mode: FlashMode)

    suspend fun setTimerDelay(delay: TimerDelay)

    suspend fun setAspectRatio(ratio: AspectRatioMode)

    suspend fun setPhotoResolution(resolution: PhotoResolution)

    suspend fun setVideoQuality(quality: VideoQuality)

    suspend fun setHdrEnabled(enabled: Boolean)

    suspend fun setStabilizationEnabled(enabled: Boolean)

    suspend fun setAutoCopyScans(enabled: Boolean)

    suspend fun setVibrateOnScan(enabled: Boolean)

    suspend fun setBeepOnScan(enabled: Boolean)
}
