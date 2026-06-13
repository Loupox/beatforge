package com.cheminee.metronome.ui.settings

import android.content.Context
import com.cheminee.metronome.data.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsViewModelTest {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        preferencesManager = PreferencesManager(context)
        viewModel = SettingsViewModel(preferencesManager)
    }

    @Test
    fun soundEnabled_isBoundToPreferences() = runBlocking {
        assertTrue(viewModel.soundEnabled.first())

        preferencesManager.setSoundEnabled(false)
        assertFalse(viewModel.soundEnabled.first())
    }

    @Test
    fun flashEnabled_isBoundToPreferences() = runBlocking {
        assertTrue(viewModel.flashEnabled.first())

        preferencesManager.setFlashEnabled(false)
        assertFalse(viewModel.flashEnabled.first())
    }

    @Test
    fun vibrationEnabled_isBoundToPreferences() = runBlocking {
        assertFalse(viewModel.vibrationEnabled.first())

        preferencesManager.setVibrationEnabled(true)
        assertTrue(viewModel.vibrationEnabled.first())
    }

    @Test
    fun setSoundEnabled_updatesPreferences() = runBlocking {
        viewModel.setSoundEnabled(false)
        assertFalse(preferencesManager.soundEnabled.first())

        viewModel.setSoundEnabled(true)
        assertTrue(preferencesManager.soundEnabled.first())
    }

    @Test
    fun setVibrationEnabled_updatesPreferences() = runBlocking {
        viewModel.setVibrationEnabled(true)
        assertTrue(preferencesManager.vibrationEnabled.first())

        viewModel.setVibrationEnabled(false)
        assertFalse(preferencesManager.vibrationEnabled.first())
    }

    @Test
    fun setFlashEnabled_updatesPreferences() = runBlocking {
        viewModel.setFlashEnabled(false)
        assertFalse(preferencesManager.flashEnabled.first())

        viewModel.setFlashEnabled(true)
        assertTrue(preferencesManager.flashEnabled.first())
    }
}