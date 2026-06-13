package com.cheminee.metronome.metronome

import org.junit.Assert.assertEquals
import org.junit.Test

class MetronomeEngineTest {

    @Test
    fun intervalIs500MsAt120Bpm() {
        assertEquals(500L, MetronomeEngine.intervalMsFor(120))
    }

    @Test
    fun intervalIs1000MsAt60Bpm() {
        assertEquals(1000L, MetronomeEngine.intervalMsFor(60))
    }

    @Test
    fun intervalIs375MsAt160Bpm() {
        assertEquals(375L, MetronomeEngine.intervalMsFor(160))
    }

    @Test
    fun intervalIs260MsAt230Bpm() {
        assertEquals(260L, MetronomeEngine.intervalMsFor(230))
    }

    @Test
    fun intervalIs230MsAt260Bpm() {
        assertEquals(230L, MetronomeEngine.intervalMsFor(260))
    }

    @Test
    fun intervalIs200MsAt300Bpm() {
        assertEquals(200L, MetronomeEngine.intervalMsFor(300))
    }

    @Test
    fun intervalCalculationIsPrecise() {
        assertEquals(60_000L, MetronomeEngine.intervalMsFor(1000) * 1000)
        assertEquals(60_000L, MetronomeEngine.intervalMsFor(500) * 500)
        assertEquals(60_000L, MetronomeEngine.intervalMsFor(300) * 300)
    }
}
