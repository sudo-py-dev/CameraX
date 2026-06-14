package com.camerax.data.repository

import com.camerax.data.local.PreferencesManager
import com.camerax.domain.model.AspectRatioMode
import com.camerax.domain.model.FlashMode
import com.camerax.domain.model.Language
import com.camerax.domain.model.PhotoResolution
import com.camerax.domain.model.ThemeMode
import com.camerax.domain.model.TimerDelay
import com.camerax.domain.model.VideoQuality
import com.camerax.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepositoryImpl(
    private val prefs: PreferencesManager,
) : PreferencesRepository {
    override val themeMode: Flow<ThemeMode>
        get() =
            prefs.themeMode.map { name ->
                runCatching { ThemeMode.valueOf(name) }.getOrDefault(ThemeMode.SYSTEM)
            }

    override val language: Flow<Language>
        get() =
            prefs.language.map { name ->
                runCatching { Language.valueOf(name) }.getOrDefault(Language.SYSTEM)
            }

    override val gridEnabled: Flow<Boolean> get() = prefs.gridEnabled
    override val shutterSoundEnabled: Flow<Boolean> get() = prefs.shutterSound

    override val flashMode: Flow<FlashMode>
        get() =
            prefs.flashMode.map { name ->
                runCatching { FlashMode.valueOf(name) }.getOrDefault(FlashMode.OFF)
            }

    override val timerDelay: Flow<TimerDelay>
        get() =
            prefs.timerDelay.map { name ->
                runCatching { TimerDelay.valueOf(name) }.getOrDefault(TimerDelay.OFF)
            }

    override val aspectRatio: Flow<AspectRatioMode>
        get() =
            prefs.aspectRatio.map { name ->
                runCatching { AspectRatioMode.valueOf(name) }.getOrDefault(AspectRatioMode.RATIO_4_3)
            }

    override val photoResolution: Flow<PhotoResolution>
        get() =
            prefs.photoResolution.map { name ->
                runCatching { PhotoResolution.valueOf(name) }.getOrDefault(PhotoResolution.HIGH)
            }

    override val videoQuality: Flow<VideoQuality>
        get() =
            prefs.videoQuality.map { name ->
                runCatching { VideoQuality.valueOf(name) }.getOrDefault(VideoQuality.FHD_1080)
            }

    override val hdrEnabled: Flow<Boolean> get() = prefs.hdrEnabled
    override val stabilizationEnabled: Flow<Boolean> get() = prefs.stabilization
    override val autoCopyScans: Flow<Boolean> get() = prefs.autoCopyScans
    override val vibrateOnScan: Flow<Boolean> get() = prefs.vibrateOnScan
    override val beepOnScan: Flow<Boolean> get() = prefs.beepOnScan

    override suspend fun setThemeMode(mode: ThemeMode) = prefs.putString("theme_mode", mode.name)

    override suspend fun setLanguage(language: Language) = prefs.putString("language", language.name)

    override suspend fun setGridEnabled(enabled: Boolean) = prefs.putBoolean("grid_enabled", enabled)

    override suspend fun setShutterSoundEnabled(enabled: Boolean) = prefs.putBoolean("shutter_sound", enabled)

    override suspend fun setFlashMode(mode: FlashMode) = prefs.putString("flash_mode", mode.name)

    override suspend fun setTimerDelay(delay: TimerDelay) = prefs.putString("timer_delay", delay.name)

    override suspend fun setAspectRatio(ratio: AspectRatioMode) = prefs.putString("aspect_ratio", ratio.name)

    override suspend fun setPhotoResolution(resolution: PhotoResolution) = prefs.putString("photo_resolution", resolution.name)

    override suspend fun setVideoQuality(quality: VideoQuality) = prefs.putString("video_quality", quality.name)

    override suspend fun setHdrEnabled(enabled: Boolean) = prefs.putBoolean("hdr_enabled", enabled)

    override suspend fun setStabilizationEnabled(enabled: Boolean) = prefs.putBoolean("stabilization", enabled)

    override suspend fun setAutoCopyScans(enabled: Boolean) = prefs.putBoolean("auto_copy_scans", enabled)

    override suspend fun setVibrateOnScan(enabled: Boolean) = prefs.putBoolean("vibrate_on_scan", enabled)

    override suspend fun setBeepOnScan(enabled: Boolean) = prefs.putBoolean("beep_on_scan", enabled)
}
