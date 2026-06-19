package com.cheminee.metronome.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATION_ENABLED, false))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _flashEnabled = MutableStateFlow(prefs.getBoolean(KEY_FLASH_ENABLED, true))
    val flashEnabled: StateFlow<Boolean> = _flashEnabled.asStateFlow()

    private val _flashColorIndex = MutableStateFlow(prefs.getInt(KEY_FLASH_COLOR_INDEX, 0))
    val flashColorIndex: StateFlow<Int> = _flashColorIndex.asStateFlow()

    private val _darkThemeEnabled = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME_ENABLED, false))
    val darkThemeEnabled: StateFlow<Boolean> = _darkThemeEnabled.asStateFlow()

    private val _accentFirstBeatEnabled = MutableStateFlow(prefs.getBoolean(KEY_ACCENT_FIRST_BEAT_ENABLED, true))
    val accentFirstBeatEnabled: StateFlow<Boolean> = _accentFirstBeatEnabled.asStateFlow()

    private val _timeSignature = MutableStateFlow(
        TimeSignature.fromString(
            prefs.getString(KEY_TIME_SIGNATURE, TimeSignature.DEFAULT.presetType.name)
                ?: TimeSignature.DEFAULT.presetType.name,
            prefs.getInt(KEY_CUSTOM_NUMERATOR, 4),
            prefs.getInt(KEY_CUSTOM_BEAT_UNIT, 4)
        )
    )
    val timeSignature: StateFlow<TimeSignature> = _timeSignature.asStateFlow()

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }

    fun setFlashEnabled(enabled: Boolean) {
        _flashEnabled.value = enabled
        prefs.edit().putBoolean(KEY_FLASH_ENABLED, enabled).apply()
    }

    fun setFlashColorIndex(index: Int) {
        _flashColorIndex.value = index
        prefs.edit().putInt(KEY_FLASH_COLOR_INDEX, index).apply()
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        _darkThemeEnabled.value = enabled
        prefs.edit().putBoolean(KEY_DARK_THEME_ENABLED, enabled).apply()
    }

    fun setAccentFirstBeatEnabled(enabled: Boolean) {
        _accentFirstBeatEnabled.value = enabled
        prefs.edit().putBoolean(KEY_ACCENT_FIRST_BEAT_ENABLED, enabled).apply()
    }

    fun setTimeSignature(timeSignature: TimeSignature) {
        _timeSignature.value = timeSignature
        when (timeSignature) {
            is TimeSignature.Preset -> {
                prefs.edit().putString(KEY_TIME_SIGNATURE, timeSignature.presetType.name).apply()
            }
            is TimeSignature.Custom -> {
                prefs.edit().putString(KEY_TIME_SIGNATURE, "CUSTOM").apply()
                prefs.edit().putInt(KEY_CUSTOM_NUMERATOR, timeSignature.numerator).apply()
                prefs.edit().putInt(KEY_CUSTOM_BEAT_UNIT, timeSignature.beatUnit).apply()
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "cheminee_prefs"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_FLASH_ENABLED = "flash_enabled"
        private const val KEY_FLASH_COLOR_INDEX = "flash_color_index"
        private const val KEY_DARK_THEME_ENABLED = "dark_theme_enabled"
        private const val KEY_ACCENT_FIRST_BEAT_ENABLED = "accent_first_beat_enabled"
        private const val KEY_TIME_SIGNATURE = "time_signature"
        private const val KEY_CUSTOM_NUMERATOR = "custom_numerator"
        private const val KEY_CUSTOM_BEAT_UNIT = "custom_beat_unit"

        val FLASH_COLORS = listOf(
            0xFFff0040.toInt(),  // Rouge
            0xFFff6600.toInt(),  // Orange
            0xFFffee00.toInt(),  // Jaune
            0xFF00ff88.toInt(),  // Vert
            0xFF00cfff.toInt(),  // Cyan
            0xFFcc00ff.toInt(),  // Violet
            0xFFff00cc.toInt(),  // Rose
            0xFFffffff.toInt()   // Blanc
        )
    }
}