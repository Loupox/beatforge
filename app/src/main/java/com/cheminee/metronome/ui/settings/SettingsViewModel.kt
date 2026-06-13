package com.cheminee.metronome.ui.settings

import androidx.lifecycle.ViewModel
import com.cheminee.metronome.data.PreferencesManager
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    val soundEnabled: StateFlow<Boolean> = preferencesManager.soundEnabled
    val flashEnabled: StateFlow<Boolean> = preferencesManager.flashEnabled
    val vibrationEnabled: StateFlow<Boolean> = preferencesManager.vibrationEnabled

    fun setSoundEnabled(enabled: Boolean) {
        preferencesManager.setSoundEnabled(enabled)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        preferencesManager.setVibrationEnabled(enabled)
    }

    fun setFlashEnabled(enabled: Boolean) {
        preferencesManager.setFlashEnabled(enabled)
    }
}