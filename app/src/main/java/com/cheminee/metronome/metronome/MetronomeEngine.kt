package com.cheminee.metronome.metronome

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.cheminee.metronome.R
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.data.TimeSignature
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

    private var _currentBeatsPerBar: Int = DEFAULT_BEATS_PER_BAR
    private var _currentBeat = 0
    private var _intervalMs: Long = 500L
    private var _currentAccentPattern: List<Boolean> = listOf(true, false, false, false)
    private var _currentBeatUnit: Int = 4

    val currentBeatsPerBar: Int get() = _currentBeatsPerBar
    val currentBeatUnit: Int get() = _currentBeatUnit
    val currentTimeSignatureDisplay: String get() = "${_currentBeatsPerBar}/${_currentBeatUnit}"

    private var preferencesManager: PreferencesManager? = null
    private var soundPool: SoundPool? = null
    private var soundNormal: Int = 0
    private var soundAccent: Int = 0
    private var soundsLoaded = false
    private var vibrator: Vibrator? = null

    fun setScope(@Suppress("UNUSED_PARAMETER") newScope: kotlinx.coroutines.CoroutineScope) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
            Log.d("MetronomeEngine", "setScope: Handler created")
        }
    }

    fun setPreferences(pm: PreferencesManager) {
        preferencesManager = pm
        Log.d("MetronomeEngine", "setPreferences: accentFirstBeatEnabled=${pm.accentFirstBeatEnabled.value}")
    }

    fun setContext(context: Context) {
        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) {
                    Log.d("MetronomeEngine", "SoundPool: sound loaded successfully")
                } else {
                    Log.e("MetronomeEngine", "SoundPool: sound load failed with status=$status")
                }
            }

            soundNormal = soundPool?.load(context, R.raw.click_normal, 1) ?: 0
            soundAccent = soundPool?.load(context, R.raw.click_accent, 1) ?: 0
            soundsLoaded = true
            Log.d("MetronomeEngine", "setContext: SoundPool created, normal=$soundNormal, accent=$soundAccent")
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
        val pm = preferencesManager
        if (pm == null) {
            Log.e("MetronomeEngine", "playSound: preferencesManager is NULL!")
            return
        }
        if (!pm.soundEnabled.value) {
            Log.d("MetronomeEngine", "playSound: sound disabled, skipping")
            return
        }
        val beatInBar = beat % _currentBeatsPerBar
        val isAccentedFromPattern = _currentAccentPattern.getOrElse(beatInBar) { false }
        val accentEnabled = pm.accentFirstBeatEnabled.value
        val isAccented = isAccentedFromPattern && accentEnabled

        val soundId = if (isAccented && soundAccent != 0) soundAccent else soundNormal
        val volume = if (isAccented) 1.0f else 0.7f

        soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        Log.d("MetronomeEngine", "playSound: beat=$beat, beatInBar=$beatInBar, accent=$isAccented, soundId=$soundId")
    }

    private fun playVibrationIfEnabled(beat: Int) {
        val pm = preferencesManager ?: return
        if (!pm.vibrationEnabled.value) return
        val beatInBar = beat % _currentBeatsPerBar
        val isAccentedFromPattern = _currentAccentPattern.getOrElse(beatInBar) { false }
        val accentEnabled = pm.accentFirstBeatEnabled.value
        val isAccented = isAccentedFromPattern && accentEnabled
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(30, if (isAccented) 255 else 128))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(30)
            }
        }
        Log.d("MetronomeEngine", "playVibration: beat=$beat, beatInBar=$beatInBar, accent=$isAccented")
    }

    fun start(bpm: Int, timeSignature: TimeSignature = TimeSignature.DEFAULT) {
        val h = handler
        Log.d("MetronomeEngine", "start: bpm=$bpm, timeSignature=${timeSignature.displayName}, handler=${h != null}")
        if (h == null) {
            Log.e("MetronomeEngine", "start: handler is null, cannot start!")
            return
        }
        tickId++
        currentTickId = tickId
        stop()
        if (bpm <= 0) return
        _currentBpm.value = bpm
        _currentBeatsPerBar = timeSignature.numerator
        _currentBeatUnit = timeSignature.beatUnit
        _currentAccentPattern = timeSignature.accentPattern
        _intervalMs = intervalMsForWithBeatUnit(bpm, timeSignature.beatUnit)
        val flashMs = (_intervalMs / 2).coerceIn(50L, 200L)
        Log.d("MetronomeEngine", "Starting: bpm=$bpm, intervalMs=$_intervalMs, flashMs=$flashMs, tickId=$currentTickId")
        _running.value = true
        _currentBeat = 0
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
                @Suppress("UNUSED_VARIABLE")
                val scheduledTime = SystemClock.elapsedRealtime()

                _beatIndex.value = _currentBeat % _currentBeatsPerBar
                _flash.value = true
                if (_currentBeat != lastEmittedBeat) {
                    lastEmittedBeat = _currentBeat
                    _beatTrigger.value = _currentBeat
                    playSoundIfEnabled(_currentBeat)
                    playVibrationIfEnabled(_currentBeat)
                }
                Log.d("MetronomeEngine", "Flash ON: beat=$_currentBeat, tickId=$myTickId")

                currentHandler.postDelayed({
                    if (myTickId != currentTickId) {
                        Log.d("MetronomeEngine", "Stale flash-off ignored, tickId=$myTickId")
                        return@postDelayed
                    }
                    if (!_running.value) return@postDelayed
                    _flash.value = false
                    Log.d("MetronomeEngine", "Flash OFF, next beat in ${_intervalMs - flashMs}ms, tickId=$myTickId")
                    _currentBeat++
                    currentHandler.postDelayed(currentRunnable, _intervalMs - flashMs)
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

    @JvmStatic
    fun intervalMsForWithBeatUnit(bpm: Int, beatUnit: Int): Long {
        val beatUnitRatio = beatUnit.toDouble() / 4.0
        return (60_000L / bpm / beatUnitRatio).toLong().coerceAtLeast(50L)
    }
}