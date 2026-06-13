package com.cheminee.metronome.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PreferencesManagerTest {

    private lateinit var prefsManager: PreferencesManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        prefsManager = PreferencesManager(context)
    }

    @Test
    fun soundEnabled_defaultsToTrue() = runBlocking {
        val value = prefsManager.soundEnabled.first()
        assertTrue(value)
    }

    @Test
    fun vibrationEnabled_defaultsToFalse() = runBlocking {
        val value = prefsManager.vibrationEnabled.first()
        assertFalse(value)
    }

    @Test
    fun flashEnabled_defaultsToTrue() = runBlocking {
        val value = prefsManager.flashEnabled.first()
        assertTrue(value)
    }

    @Test
    fun flashColorIndex_defaultsToZero() = runBlocking {
        val value = prefsManager.flashColorIndex.first()
        assertEquals(0, value)
    }

    @Test
    fun setSoundEnabled_updatesFlow() = runBlocking {
        prefsManager.setSoundEnabled(false)
        val value = prefsManager.soundEnabled.first()
        assertFalse(value)

        prefsManager.setSoundEnabled(true)
        val restored = prefsManager.soundEnabled.first()
        assertTrue(restored)
    }

    @Test
    fun setVibrationEnabled_updatesFlow() = runBlocking {
        prefsManager.setVibrationEnabled(true)
        val value = prefsManager.vibrationEnabled.first()
        assertTrue(value)
    }

    @Test
    fun setFlashEnabled_updatesFlow() = runBlocking {
        prefsManager.setFlashEnabled(false)
        val value = prefsManager.flashEnabled.first()
        assertFalse(value)
    }

    @Test
    fun setFlashColorIndex_updatesFlow() = runBlocking {
        prefsManager.setFlashColorIndex(3)
        val value = prefsManager.flashColorIndex.first()
        assertEquals(3, value)
    }

    @Test
    fun flashColors_listHasEightColors() {
        assertEquals(8, PreferencesManager.FLASH_COLORS.size)
    }

    @Test
    fun flashColors_indicesAreValid() {
        for (i in 0 until 8) {
            val color = PreferencesManager.FLASH_COLORS[i]
            assertTrue("Color at index $i should be valid", (color shr 24) != 0 || color != 0)
        }
    }
}