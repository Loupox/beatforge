package com.cheminee.metronome.ui.metronome

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.data.TimeSignature
import com.cheminee.metronome.metronome.MetronomeEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class StandaloneMetronomeViewModel(
    private val preferencesManager: PreferencesManager? = null
) : ViewModel() {

    val engine: MetronomeEngine = MetronomeEngine

    val flashEnabled = preferencesManager?.flashEnabled
    val flashColorIndex = preferencesManager?.flashColorIndex
    val soundEnabled = preferencesManager?.soundEnabled
    val vibrationEnabled = preferencesManager?.vibrationEnabled
    val accentFirstBeatEnabled = preferencesManager?.accentFirstBeatEnabled
    val timeSignature = preferencesManager?.timeSignature

    fun toggleSound() {
        val current = preferencesManager?.soundEnabled?.value ?: true
        preferencesManager?.setSoundEnabled(!current)
    }

    fun toggleVibration() {
        val current = preferencesManager?.vibrationEnabled?.value ?: false
        preferencesManager?.setVibrationEnabled(!current)
    }

    private interface IntStateHolder { var intValue: Int }
    private val _bpm: IntStateHolder = try {
        val state = androidx.compose.runtime.mutableIntStateOf(120)
        object : IntStateHolder {
            override var intValue: Int
                get() = state.intValue
                set(value) { state.intValue = value }
        }
    } catch (t: Throwable) {
        object : IntStateHolder { override var intValue: Int = 120 }
    }
    val bpm: Int get() = _bpm.intValue

    private var bpmSyncJob: Job? = null

    private val tapTimestamps = mutableListOf<Long>()
    private var tapResetJob: Job? = null
    private val tapTimeoutMs = 2000L

    init {
        engine.setScope(viewModelScope)
    }

    fun onTap() {
        val now = SystemClock.elapsedRealtime()
        tapResetJob?.cancel()
        tapTimestamps.add(now)
        if (tapTimestamps.size > 8) {
            tapTimestamps.removeAt(0)
        }
        if (tapTimestamps.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 1 until tapTimestamps.size) {
                val interval = tapTimestamps[i] - tapTimestamps[i - 1]
                if (interval in 50..2000) {
                    intervals.add(interval)
                }
            }
            if (intervals.isNotEmpty()) {
                val avgInterval = intervals.average()
                val calculatedBpm = (60000.0 / avgInterval).toInt().coerceIn(45, 250)
                setBpm(calculatedBpm)
            }
        }
        tapResetJob = viewModelScope.launch {
            delay(tapTimeoutMs)
            tapTimestamps.clear()
        }
    }

    protected open fun initialize() {
        MetronomeEngine.setScope(viewModelScope)
        _bpm.intValue = engine.currentBpm.value

        bpmSyncJob = viewModelScope.launch {
            engine.currentBpm.collect { newBpm ->
                if (!engine.running.value) {
                    _bpm.intValue = newBpm
                }
            }
        }
    }

    init {
        initialize()
    }

    fun setBpm(value: Int) {
        _bpm.intValue = value.coerceIn(45, 250)
        if (engine.running.value) {
            val ts = preferencesManager?.timeSignature?.value ?: TimeSignature.FOUR_FOUR
            engine.start(_bpm.intValue, ts)
        }
    }

    fun incrementBpm() = setBpm(bpm + 1)
    fun decrementBpm() = setBpm(bpm - 1)

    fun getFinalBpm(): Int {
        val current = _bpm.intValue
        val niceValues = listOf(45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150, 155, 160, 165, 170, 175, 180, 185, 190, 195, 200, 205, 210, 215, 220, 225, 230, 235, 240, 245, 250)
        val closest = niceValues.minByOrNull { kotlin.math.abs(it - current) } ?: current
        return if (kotlin.math.abs(closest - current) <= 5) closest else current
    }

    fun setFlashColorIndex(index: Int) {
        preferencesManager?.setFlashColorIndex(index)
    }

    fun toggle() {
        viewModelScope.launch {
            if (engine.running.value) {
                engine.stop()
            } else {
                val ts = preferencesManager?.timeSignature?.value ?: TimeSignature.DEFAULT
                engine.start(_bpm.intValue, ts)
            }
        }
    }

    fun setTimeSignature(timeSignature: TimeSignature) {
        preferencesManager?.setTimeSignature(timeSignature)
        if (engine.running.value) {
            engine.start(_bpm.intValue, timeSignature)
        }
    }

    fun setCustomTimeSignature(numerator: Int, beatUnit: Int) {
        val customTs = TimeSignature.Custom.create(numerator, beatUnit)
        preferencesManager?.setTimeSignature(customTs)
        if (engine.running.value) {
            engine.start(_bpm.intValue, customTs)
        }
    }

    override fun onCleared() {
        Log.d("MetronomeEngine", "StandaloneVM onCleared")
        bpmSyncJob?.cancel()
        engine.stop()
        super.onCleared()
    }
}