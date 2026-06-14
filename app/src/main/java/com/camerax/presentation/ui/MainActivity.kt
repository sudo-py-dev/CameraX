package com.camerax.presentation.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.camerax.CameraXApplication
import com.camerax.presentation.navigation.AppNavigation
import com.camerax.presentation.theme.CameraXTheme
import com.camerax.presentation.ui.camera.CameraViewModel
import com.camerax.presentation.ui.gallery.GalleryViewModel
import com.camerax.presentation.ui.permissions.PermissionScreen
import com.camerax.presentation.ui.scanner.ScanHistoryViewModel
import com.camerax.presentation.ui.settings.SettingsViewModel
import com.camerax.presentation.ui.util.LocaleHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CameraXApplication
        val container = app.container

        val factory =
            viewModelFactory {
                initializer {
                    CameraViewModel(
                        container.preferencesRepository,
                        container.mediaRepository,
                        container.scanHistoryRepository,
                    )
                }
                initializer {
                    GalleryViewModel(container.mediaRepository)
                }
                initializer {
                    ScanHistoryViewModel(container.scanHistoryRepository)
                }
                initializer {
                    SettingsViewModel(container.preferencesRepository)
                }
            }

        val cameraViewModel = ViewModelProvider(this, factory)[CameraViewModel::class.java]
        val galleryViewModel = ViewModelProvider(this, factory)[GalleryViewModel::class.java]
        val scanHistoryViewModel = ViewModelProvider(this, factory)[ScanHistoryViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val language by settingsViewModel.language.collectAsState()

            LocaleHelper.applyLocale(this, language.code)

            CameraXTheme(themeMode = themeMode) {
                val permissions =
                    buildList {
                        add(Manifest.permission.CAMERA)
                        add(Manifest.permission.RECORD_AUDIO)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                val permissionState = rememberMultiplePermissionsState(permissions)

                val cameraGranted =
                    permissionState.permissions.any {
                        it.permission == Manifest.permission.CAMERA && it.status.isGranted
                    }

                if (cameraGranted) {
                    AppNavigation(
                        cameraViewModel = cameraViewModel,
                        galleryViewModel = galleryViewModel,
                        scanHistoryViewModel = scanHistoryViewModel,
                        settingsViewModel = settingsViewModel,
                    )
                } else {
                    PermissionScreen(
                        onRequestPermissions = {
                            permissionState.launchMultiplePermissionRequest()
                        },
                    )
                }
            }
        }
    }
}
