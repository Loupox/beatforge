package com.cheminee.metronome.metronome

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.cheminee.metronome.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_BEATS_PER_BAR = 4

object MetronomeEngine {
    private var handler: Handler? = null
    private var tickId: Int = 0
    private var currentTickId: Int = 0

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

    private var preferencesManager: PreferencesManager? = null
    private var toneGenerator: ToneGenerator? = null

    fun setScope(@Suppress("UNUSED_PARAMETER") newScope: kotlinx.coroutines.CoroutineScope) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
            Log.d("MetronomeEngine", "setScope: Handler created")
        }
    }

    fun setPreferences(pm: PreferencesManager) {
        preferencesManager = pm
    }

    fun setContext(@Suppress("UNUSED_PARAMETER") context: Context) {
        if (toneGenerator == null) {
            @Suppress("DEPRECATION")
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            } catch (e: Throwable) {
                Log.e("MetronomeEngine", "ToneGenerator unavailable", e)
                null
            }
            Log.d("MetronomeEngine", "setContext: ToneGenerator created = ${toneGenerator != null}")
        }
    }

    private fun playSoundIfEnabled(beat: Int) {
        val pm = preferencesManager ?: return
        if (!pm.soundEnabled.value) return
        val isFirstBeat = beat % 4 == 0
        val tone = if (isFirstBeat) ToneGenerator.TONE_PROP_BEEP2 else ToneGenerator.TONE_PROP_BEEP
        val duration = if (isFirstBeat) 100 else 75
        toneGenerator?.startTone(tone, duration)
        Log.d("MetronomeEngine", "playSound: beat=$beat, tone=$tone, duration=$duration")
    }

    fun start(bpm: Int, beatsPerBar: Int = DEFAULT_BEATS_PER_BAR) {
        val h = handler
        Log.d("MetronomeEngine", "start: bpm=$bpm, handler=${h != null}")
        if (h == null) {
            Log.e("MetronomeEngine", "start: handler is null, cannot start!")
            return
        }
        tickId++
        currentTickId = tickId
        stop()
        if (bpm <= 0) return
        _currentBpm.value = bpm
        currentBeatsPerBar = beatsPerBar
        intervalMs = intervalMsFor(bpm)
        val flashMs = (intervalMs / 2).coerceIn(50L, 200L)
        Log.d("MetronomeEngine", "Starting: bpm=$bpm, intervalMs=$intervalMs, flashMs=$flashMs, tickId=$currentTickId")
        _running.value = true
        currentBeat = 0
        lastEmittedBeat = -1

        val runnable = object : Runnable {
            private val myTickId = currentTickId
            override fun run() {
                if (myTickId != currentTickId) {
                    Log.d("MetronomeEngine", "Stale tick ignored, tickId=$myTickId vs currentTickId=$currentTickId")
                    return
                }
                if (!_running.value) return

                val currentHandler = handler ?: return
                val currentRunnable = this
                val scheduledTime = SystemClock.elapsedRealtime()

                _beatIndex.value = currentBeat % currentBeatsPerBar
                _flash.value = true
                if (currentBeat != lastEmittedBeat) {
                    lastEmittedBeat = currentBeat
                    _beatTrigger.value = currentBeat
                    playSoundIfEnabled(currentBeat)
                }
                Log.d("MetronomeEngine", "Flash ON: beat=$currentBeat, tickId=$myTickId")

                currentHandler.postDelayed({
                    if (myTickId != currentTickId) {
                        Log.d("MetronomeEngine", "Stale flash-off ignored, tickId=$myTickId")
                        return@postDelayed
                    }
                    if (!_running.value) return@postDelayed
                    _flash.value = false
                    Log.d("MetronomeEngine", "Flash OFF, next beat in ${intervalMs - flashMs}ms, tickId=$myTickId")
                    currentBeat++
                    currentHandler.postDelayed(currentRunnable, intervalMs - flashMs)
                }, flashMs)
            }
        }
        handler?.post(runnable)
    }

    fun stop() {
        Log.d("MetronomeEngine", "Stopping, tickId=$currentTickId")
        handler?.removeCallbacksAndMessages(null)
        _flash.value = false
        _beatIndex.value = 0
        _running.value = false
    }

    @JvmStatic
    fun intervalMsFor(bpm: Int): Long = 60_000L / bpm
}