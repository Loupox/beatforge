package com.cheminee.metronome.metronome

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
    private var toneGeneratorAccent: ToneGenerator? = null
    private var vibrator: Vibrator? = null

    fun setScope(@Suppress("UNUSED_PARAMETER") newScope: kotlinx.coroutines.CoroutineScope) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
            Log.d("MetronomeEngine", "setScope: Handler created")
        }
    }

    fun setPreferences(pm: PreferencesManager) {
        preferencesManager = pm
    }

    fun setContext(context: Context) {
        if (toneGenerator == null) {
            @Suppress("DEPRECATION")
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 60)
            } catch (e: Throwable) {
                Log.e("MetronomeEngine", "ToneGenerator unavailable", e)
                null
            }
            Log.d("MetronomeEngine", "setContext: ToneGenerator normal created = ${toneGenerator != null}")
        }
        if (toneGeneratorAccent == null) {
            @Suppress("DEPRECATION")
            toneGeneratorAccent = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            } catch (e: Throwable) {
                Log.e("MetronomeEngine", "ToneGenerator accent unavailable", e)
                null
            }
            Log.d("MetronomeEngine", "setContext: ToneGenerator accent created = ${toneGeneratorAccent != null}")
        }
        if (vibrator == null) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            Log.d("MetronomeEngine", "setContext: Vibrator created = ${vibrator != null}")
        }
    }

    private fun playSoundIfEnabled(beat: Int) {
        val pm = preferencesManager ?: return
        if (!pm.soundEnabled.value) return
        val isFirstBeat = beat % currentBeatsPerBar == 0
        val accentEnabled = pm.accentFirstBeatEnabled.value
        val isAccented = isFirstBeat && accentEnabled
        val duration = if (isAccented) 150 else 75
        val generator = if (isAccented) toneGeneratorAccent else toneGenerator
        generator?.startTone(ToneGenerator.TONE_PROP_BEEP2, duration)
        Log.d("MetronomeEngine", "playSound: beat=$beat, isFirst=$isFirstBeat, accent=$isAccented, vol=${if (isAccented) 100 else 60}, duration=$duration")
    }

    private fun playVibrationIfEnabled(beat: Int) {
        val pm = preferencesManager ?: return
        if (!pm.vibrationEnabled.value) return
        val isFirstBeat = beat % currentBeatsPerBar == 0
        val accentEnabled = pm.accentFirstBeatEnabled.value
        val isAccented = isFirstBeat && accentEnabled
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(30, if (isAccented) 255 else 128))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(30)
            }
        }
        Log.d("MetronomeEngine", "playVibration: beat=$beat, isFirst=$isFirstBeat, accent=$isAccented")
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
                    playVibrationIfEnabled(currentBeat)
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