package com.camerax.presentation.ui.settings

import app.cash.turbine.test
import com.camerax.MainDispatcherRule
import com.camerax.domain.model.Language
import com.camerax.domain.model.ThemeMode
import com.camerax.domain.repository.PreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: PreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val languageFlow = MutableStateFlow(Language.SYSTEM)

    @Before
    fun setup() {
        repository =
            mockk(relaxed = true) {
                coEvery { themeMode } returns themeModeFlow
                coEvery { language } returns languageFlow
                // Provide default flows for other properties to avoid crashes
                coEvery { gridEnabled } returns MutableStateFlow(false)
                coEvery { shutterSoundEnabled } returns MutableStateFlow(true)
                coEvery { flashMode } returns MutableStateFlow(com.camerax.domain.model.FlashMode.OFF)
                coEvery { timerDelay } returns MutableStateFlow(com.camerax.domain.model.TimerDelay.OFF)
                coEvery { aspectRatio } returns MutableStateFlow(com.camerax.domain.model.AspectRatioMode.RATIO_4_3)
                coEvery { photoResolution } returns MutableStateFlow(com.camerax.domain.model.PhotoResolution.HIGH)
                coEvery { videoQuality } returns MutableStateFlow(com.camerax.domain.model.VideoQuality.FHD_1080)
                coEvery { hdrEnabled } returns MutableStateFlow(false)
                coEvery { stabilizationEnabled } returns MutableStateFlow(true)
                coEvery { autoCopyScans } returns MutableStateFlow(false)
                coEvery { vibrateOnScan } returns MutableStateFlow(true)
                coEvery { beepOnScan } returns MutableStateFlow(true)
            }
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun `themeMode updates correctly when repository emits new value`() =
        runTest {
            viewModel.themeMode.test {
                assertEquals(ThemeMode.SYSTEM, awaitItem())

                themeModeFlow.value = ThemeMode.DARK
                assertEquals(ThemeMode.DARK, awaitItem())
            }
        }

    @Test
    fun `setThemeMode calls repository`() =
        runTest {
            viewModel.setThemeMode(ThemeMode.LIGHT)
            coVerify(exactly = 1) { repository.setThemeMode(ThemeMode.LIGHT) }
        }

    @Test
    fun `language updates correctly when repository emits new value`() =
        runTest {
            viewModel.language.test {
                assertEquals(Language.SYSTEM, awaitItem())

                languageFlow.value = Language.SPANISH
                assertEquals(Language.SPANISH, awaitItem())
            }
        }

    @Test
    fun `setLanguage calls repository`() =
        runTest {
            viewModel.setLanguage(Language.FRENCH)
            coVerify(exactly = 1) { repository.setLanguage(Language.FRENCH) }
        }
}
