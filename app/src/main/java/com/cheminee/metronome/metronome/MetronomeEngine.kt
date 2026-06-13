package com.cheminee.metronome.metronome

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_BEATS_PER_BAR = 4

object MetronomeEngine {
    private var handler: Handler? = null
    private var tickRunnable: Runnable? = null

    private val _flash = MutableStateFlow(false)
    val flash: StateFlow<Boolean> = _flash

    private val _beatIndex = MutableStateFlow(0)
    val beatIndex: StateFlow<Int> = _beatIndex

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running

    private var lastEmittedBeat = -1
    private val _beatTrigger = MutableStateFlow(0)
    val beatTrigger: StateFlow<Int> = _beatTrigger

    private val _currentBpm = MutableStateFlow(120)
    val currentBpm: StateFlow<Int> = _currentBpm

    private var currentBeatsPerBar: Int = DEFAULT_BEATS_PER_BAR
    private var currentBeat = 0
    private var intervalMs: Long = 500L

    fun setScope(@Suppress("UNUSED_PARAMETER") newScope: kotlinx.coroutines.CoroutineScope) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
            Log.d("MetronomeEngine", "setScope: Handler created")
        }
    }

    fun start(bpm: Int, beatsPerBar: Int = DEFAULT_BEATS_PER_BAR) {
        val h = handler
        Log.d("MetronomeEngine", "start: bpm=$bpm, handler=${h != null}")
        if (h == null) {
            Log.e("MetronomeEngine", "start: handler is null, cannot start!")
            return
        }
        stop()
        if (bpm <= 0) return
        _currentBpm.value = bpm
        currentBeatsPerBar = beatsPerBar
        intervalMs = intervalMsFor(bpm)
        val flashMs = (intervalMs / 2).coerceIn(50L, 200L)
        Log.d("MetronomeEngine", "Starting: bpm=$bpm, intervalMs=$intervalMs, flashMs=$flashMs")
        _running.value = true
        currentBeat = 0
        lastEmittedBeat = -1

        val runnable = object : Runnable {
            override fun run() {
                if (!_running.value) return

                val currentHandler = handler ?: return
                val currentRunnable = this

                _beatIndex.value = currentBeat % currentBeatsPerBar
                _flash.value = true
                if (currentBeat != lastEmittedBeat) {
                    lastEmittedBeat = currentBeat
                    _beatTrigger.value = currentBeat
                }
                Log.d("MetronomeEngine", "Flash ON: beat=$currentBeat")

                currentHandler.postDelayed({
                    if (!_running.value) return@postDelayed
                    _flash.value = false
                    Log.d("MetronomeEngine", "Flash OFF")
                    currentBeat++
                    currentHandler.post(currentRunnable)
                }, flashMs)
            }
        }
        tickRunnable = runnable
        handler?.post(runnable)
    }

    fun stop() {
        Log.d("MetronomeEngine", "Stopping")
        handler?.removeCallbacksAndMessages(null)
        tickRunnable = null
        _flash.value = false
        _beatIndex.value = 0
        _running.value = false
    }

    @JvmStatic
    fun intervalMsFor(bpm: Int): Long = 60_000L / bpm
}