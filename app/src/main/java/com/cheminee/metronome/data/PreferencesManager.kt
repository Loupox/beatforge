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

    companion object {
        private const val PREFS_NAME = "cheminee_prefs"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_FLASH_ENABLED = "flash_enabled"
        private const val KEY_FLASH_COLOR_INDEX = "flash_color_index"
        private const val KEY_DARK_THEME_ENABLED = "dark_theme_enabled"

        val FLASH_COLORS = listOf(
            0xFFFFEB3Bu.toInt(),  // Jaune
            0xFF00E5FFu.toInt(),  // Cyan
            0xFFFF00FFu.toInt(),  // Magenta
            0xFF76FF03u.toInt(),  // Vert lime
            0xFFFF9100u.toInt(),  // Orange
            0xFFE040FBu.toInt(),  // Violet
            0xFFFF5252u.toInt(),  // Rouge
            0xFF40C4FFu.toInt()   // Bleu clair
        )
    }
}