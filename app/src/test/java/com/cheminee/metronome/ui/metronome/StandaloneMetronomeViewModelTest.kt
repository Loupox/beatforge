package com.cheminee.metronome.ui.metronome

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StandaloneMetronomeViewModelTest {

    private fun createTestableViewModel(): StandaloneMetronomeViewModel {
        return object : StandaloneMetronomeViewModel(null) {
            protected override fun initialize() {}
        }
    }

    @Test
    fun setBpm_updates_value() {
        val viewModel = createTestableViewModel()

        viewModel.setBpm(150)

        assertEquals(150, viewModel.bpm)
    }

    @Test
    fun setBpm_clamps_to_minimum_30() {
        val viewModel = createTestableViewModel()

        viewModel.setBpm(10)

        assertEquals(30, viewModel.bpm)
    }

    @Test
    fun setBpm_clamps_to_maximum_300() {
        val viewModel = createTestableViewModel()

        viewModel.setBpm(500)

        assertEquals(300, viewModel.bpm)
    }

    @Test
    fun default_bpm_is_120() {
        val viewModel = createTestableViewModel()

        assertEquals(120, viewModel.bpm)
    }
}