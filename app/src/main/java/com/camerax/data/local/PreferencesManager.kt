package com.camerax.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "camerax_prefs")

class PreferencesManager(private val context: Context) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val GRID_ENABLED = booleanPreferencesKey("grid_enabled")
        val SHUTTER_SOUND = booleanPreferencesKey("shutter_sound")

        val FLASH_MODE = stringPreferencesKey("flash_mode")
        val TIMER_DELAY = stringPreferencesKey("timer_delay")
        val ASPECT_RATIO = stringPreferencesKey("aspect_ratio")
        val PHOTO_RESOLUTION = stringPreferencesKey("photo_resolution")
        val VIDEO_QUALITY = stringPreferencesKey("video_quality")
        val HDR_ENABLED = booleanPreferencesKey("hdr_enabled")
        val STABILIZATION = booleanPreferencesKey("stabilization")
        val AUTO_COPY_SCANS = booleanPreferencesKey("auto_copy_scans")
        val VIBRATE_ON_SCAN = booleanPreferencesKey("vibrate_on_scan")
        val BEEP_ON_SCAN = booleanPreferencesKey("beep_on_scan")
    }

    fun getString(
        key: String,
        default: String,
    ): Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(key)] ?: default
        }

    fun getBoolean(
        key: String,
        default: Boolean,
    ): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[booleanPreferencesKey(key)] ?: default
        }

    suspend fun putString(
        key: String,
        value: String,
    ) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    suspend fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        context.dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key)] = value
        }
    }

    // Typed accessors
    val themeMode: Flow<String> get() = getString("theme_mode", "SYSTEM")
    val language: Flow<String> get() = getString("language", "SYSTEM")
    val gridEnabled: Flow<Boolean> get() = getBoolean("grid_enabled", false)
    val shutterSound: Flow<Boolean> get() = getBoolean("shutter_sound", true)

    val flashMode: Flow<String> get() = getString("flash_mode", "OFF")
    val timerDelay: Flow<String> get() = getString("timer_delay", "OFF")
    val aspectRatio: Flow<String> get() = getString("aspect_ratio", "RATIO_4_3")
    val photoResolution: Flow<String> get() = getString("photo_resolution", "HIGH")
    val videoQuality: Flow<String> get() = getString("video_quality", "FHD_1080")
    val hdrEnabled: Flow<Boolean> get() = getBoolean("hdr_enabled", false)
    val stabilization: Flow<Boolean> get() = getBoolean("stabilization", true)
    val autoCopyScans: Flow<Boolean> get() = getBoolean("auto_copy_scans", false)
    val vibrateOnScan: Flow<Boolean> get() = getBoolean("vibrate_on_scan", true)
    val beepOnScan: Flow<Boolean> get() = getBoolean("beep_on_scan", true)
}
