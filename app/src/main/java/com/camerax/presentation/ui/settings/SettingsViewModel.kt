package com.camerax.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerax.domain.model.AspectRatioMode
import com.camerax.domain.model.FlashMode
import com.camerax.domain.model.Language
import com.camerax.domain.model.PhotoResolution
import com.camerax.domain.model.ThemeMode
import com.camerax.domain.model.TimerDelay
import com.camerax.domain.model.VideoQuality
import com.camerax.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> =
        preferencesRepository.themeMode
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val language: StateFlow<Language> =
        preferencesRepository.language
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Language.SYSTEM)

    val gridEnabled: StateFlow<Boolean> =
        preferencesRepository.gridEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val shutterSound: StateFlow<Boolean> =
        preferencesRepository.shutterSoundEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val flashMode: StateFlow<FlashMode> =
        preferencesRepository.flashMode
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FlashMode.OFF)

    val timerDelay: StateFlow<TimerDelay> =
        preferencesRepository.timerDelay
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerDelay.OFF)

    val aspectRatio: StateFlow<AspectRatioMode> =
        preferencesRepository.aspectRatio
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AspectRatioMode.RATIO_4_3)

    val photoResolution: StateFlow<PhotoResolution> =
        preferencesRepository.photoResolution
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PhotoResolution.HIGH)

    val videoQuality: StateFlow<VideoQuality> =
        preferencesRepository.videoQuality
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VideoQuality.FHD_1080)

    val hdrEnabled: StateFlow<Boolean> =
        preferencesRepository.hdrEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val stabilization: StateFlow<Boolean> =
        preferencesRepository.stabilizationEnabled
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val autoCopyScans: StateFlow<Boolean> =
        preferencesRepository.autoCopyScans
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val vibrateOnScan: StateFlow<Boolean> =
        preferencesRepository.vibrateOnScan
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val beepOnScan: StateFlow<Boolean> =
        preferencesRepository.beepOnScan
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { preferencesRepository.setThemeMode(mode) }

    fun setLanguage(lang: Language) = viewModelScope.launch { preferencesRepository.setLanguage(lang) }

    fun setGridEnabled(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setGridEnabled(enabled) }

    fun setShutterSound(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setShutterSoundEnabled(enabled) }

    fun setAspectRatio(ratio: AspectRatioMode) = viewModelScope.launch { preferencesRepository.setAspectRatio(ratio) }

    fun setPhotoResolution(res: PhotoResolution) = viewModelScope.launch { preferencesRepository.setPhotoResolution(res) }

    fun setVideoQuality(q: VideoQuality) = viewModelScope.launch { preferencesRepository.setVideoQuality(q) }

    fun setHdrEnabled(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setHdrEnabled(enabled) }

    fun setStabilization(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setStabilizationEnabled(enabled) }

    fun setAutoCopyScans(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setAutoCopyScans(enabled) }

    fun setVibrateOnScan(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setVibrateOnScan(enabled) }

    fun setBeepOnScan(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setBeepOnScan(enabled) }
}
