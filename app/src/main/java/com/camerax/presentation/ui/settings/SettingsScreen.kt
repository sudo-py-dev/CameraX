package com.camerax.presentation.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camerax.R
import com.camerax.domain.model.AspectRatioMode
import com.camerax.domain.model.Language
import com.camerax.domain.model.PhotoResolution
import com.camerax.domain.model.ThemeMode
import com.camerax.domain.model.VideoQuality
import com.camerax.presentation.theme.CameraXTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToAbout: () -> Unit,
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val gridEnabled by viewModel.gridEnabled.collectAsState()
    val shutterSound by viewModel.shutterSound.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    val photoResolution by viewModel.photoResolution.collectAsState()
    val videoQuality by viewModel.videoQuality.collectAsState()
    val hdrEnabled by viewModel.hdrEnabled.collectAsState()
    val stabilization by viewModel.stabilization.collectAsState()
    val autoCopyScans by viewModel.autoCopyScans.collectAsState()
    val vibrateOnScan by viewModel.vibrateOnScan.collectAsState()
    val beepOnScan by viewModel.beepOnScan.collectAsState()

    var themeExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    var aspectExpanded by remember { mutableStateOf(false) }
    var resolutionExpanded by remember { mutableStateOf(false) }
    var qualityExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CameraXTheme.gradients.backgroundGradient)
                .padding(20.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // General
            SectionHeader(stringResource(R.string.settings_general))
            SettingsCard {
                SettingsDropdownItem(
                    title = stringResource(R.string.setting_theme),
                    subtitle =
                        when (themeMode) {
                            ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                            ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                            ThemeMode.DARK -> stringResource(R.string.theme_dark)
                        },
                    icon = Icons.Default.Settings,
                    expanded = themeExpanded,
                    onExpandedChange = { themeExpanded = it },
                    onDismissRequest = { themeExpanded = false },
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val label =
                            when (mode) {
                                ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                                ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                                ThemeMode.DARK -> stringResource(R.string.theme_dark)
                            }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setThemeMode(mode)
                                themeExpanded = false
                            },
                        )
                    }
                }
                SettingsDivider()
                SettingsDropdownItem(
                    title = stringResource(R.string.setting_language),
                    subtitle =
                        when (language) {
                            Language.SYSTEM -> stringResource(R.string.lang_system)
                            Language.ENGLISH -> stringResource(R.string.lang_en)
                            Language.SPANISH -> stringResource(R.string.lang_es)
                            Language.FRENCH -> stringResource(R.string.lang_fr)
                            Language.GERMAN -> stringResource(R.string.lang_de)
                            Language.HEBREW -> stringResource(R.string.lang_he)
                        },
                    icon = Icons.Default.Language,
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = it },
                    onDismissRequest = { languageExpanded = false },
                ) {
                    Language.entries.forEach { lang ->
                        val label =
                            when (lang) {
                                Language.SYSTEM -> stringResource(R.string.lang_system)
                                Language.ENGLISH -> stringResource(R.string.lang_en)
                                Language.SPANISH -> stringResource(R.string.lang_es)
                                Language.FRENCH -> stringResource(R.string.lang_fr)
                                Language.GERMAN -> stringResource(R.string.lang_de)
                                Language.HEBREW -> stringResource(R.string.lang_he)
                            }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setLanguage(lang)
                                languageExpanded = false
                            },
                        )
                    }
                }
            }

            // Camera
            SectionHeader(stringResource(R.string.settings_camera))
            SettingsCard {
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_grid),
                    icon = Icons.Default.Tune,
                    checked = gridEnabled,
                    onCheckedChange = { viewModel.setGridEnabled(it) },
                )
                SettingsDivider()
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_shutter_sound),
                    icon = Icons.Default.VolumeUp,
                    checked = shutterSound,
                    onCheckedChange = { viewModel.setShutterSound(it) },
                )
            }

            // Photo
            SectionHeader(stringResource(R.string.settings_photo))
            SettingsCard {
                SettingsDropdownItem(
                    title = stringResource(R.string.setting_aspect_ratio),
                    subtitle =
                        when (aspectRatio) {
                            AspectRatioMode.RATIO_4_3 -> "4:3"
                            AspectRatioMode.RATIO_16_9 -> "16:9"
                            AspectRatioMode.RATIO_1_1 -> "1:1"
                        },
                    icon = Icons.Default.AspectRatio,
                    expanded = aspectExpanded,
                    onExpandedChange = { aspectExpanded = it },
                    onDismissRequest = { aspectExpanded = false },
                ) {
                    AspectRatioMode.entries.forEach { ratio ->
                        val label =
                            when (ratio) {
                                AspectRatioMode.RATIO_4_3 -> "4:3"
                                AspectRatioMode.RATIO_16_9 -> "16:9"
                                AspectRatioMode.RATIO_1_1 -> "1:1"
                            }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setAspectRatio(ratio)
                                aspectExpanded = false
                            },
                        )
                    }
                }
                SettingsDivider()
                SettingsDropdownItem(
                    title = stringResource(R.string.setting_resolution),
                    subtitle =
                        when (photoResolution) {
                            PhotoResolution.HIGH -> stringResource(R.string.quality_high)
                            PhotoResolution.MEDIUM -> stringResource(R.string.quality_medium)
                            PhotoResolution.LOW -> stringResource(R.string.quality_low)
                        },
                    icon = Icons.Default.HighQuality,
                    expanded = resolutionExpanded,
                    onExpandedChange = { resolutionExpanded = it },
                    onDismissRequest = { resolutionExpanded = false },
                ) {
                    PhotoResolution.entries.forEach { res ->
                        val label =
                            when (res) {
                                PhotoResolution.HIGH -> stringResource(R.string.quality_high)
                                PhotoResolution.MEDIUM -> stringResource(R.string.quality_medium)
                                PhotoResolution.LOW -> stringResource(R.string.quality_low)
                            }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setPhotoResolution(res)
                                resolutionExpanded = false
                            },
                        )
                    }
                }
                SettingsDivider()
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_hdr),
                    icon = Icons.Default.Hd,
                    checked = hdrEnabled,
                    onCheckedChange = { viewModel.setHdrEnabled(it) },
                )
            }

            // Video
            SectionHeader(stringResource(R.string.settings_video))
            SettingsCard {
                SettingsDropdownItem(
                    title = stringResource(R.string.setting_video_quality),
                    subtitle =
                        when (videoQuality) {
                            VideoQuality.FHD_1080 -> "1080p FHD"
                            VideoQuality.HD_720 -> "720p HD"
                            VideoQuality.SD_480 -> "480p SD"
                        },
                    icon = Icons.Default.Videocam,
                    expanded = qualityExpanded,
                    onExpandedChange = { qualityExpanded = it },
                    onDismissRequest = { qualityExpanded = false },
                ) {
                    VideoQuality.entries.forEach { q ->
                        val label =
                            when (q) {
                                VideoQuality.FHD_1080 -> "1080p FHD"
                                VideoQuality.HD_720 -> "720p HD"
                                VideoQuality.SD_480 -> "480p SD"
                            }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setVideoQuality(q)
                                qualityExpanded = false
                            },
                        )
                    }
                }
                SettingsDivider()
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_stabilization),
                    icon = Icons.Default.GraphicEq,
                    checked = stabilization,
                    onCheckedChange = { viewModel.setStabilization(it) },
                )
            }

            // QR Scanner
            SectionHeader(stringResource(R.string.settings_scanner))
            SettingsCard {
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_auto_copy),
                    icon = Icons.Default.QrCodeScanner,
                    checked = autoCopyScans,
                    onCheckedChange = { viewModel.setAutoCopyScans(it) },
                )
                SettingsDivider()
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_vibrate_scan),
                    icon = Icons.Default.Vibration,
                    checked = vibrateOnScan,
                    onCheckedChange = { viewModel.setVibrateOnScan(it) },
                )
                SettingsDivider()
                SettingsSwitchItem(
                    title = stringResource(R.string.setting_beep_scan),
                    icon = Icons.Default.Notifications,
                    checked = beepOnScan,
                    onCheckedChange = { viewModel.setBeepOnScan(it) },
                )
            }

            // About
            SectionHeader(stringResource(R.string.settings_about))
            SettingsCard {
                SettingsNavigationItem(
                    title = stringResource(R.string.setting_about_app),
                    icon = Icons.Default.Info,
                    onClick = onNavigateToAbout,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style =
            MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
            ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 12.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@Composable
private fun SettingsDropdownItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(true) }
                    .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        )
    }
}

@Composable
private fun SettingsNavigationItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}
