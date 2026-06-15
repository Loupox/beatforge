package com.cheminee.metronome.ui.metronome

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheminee.metronome.data.PreferencesManager
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
                val calculatedBpm = (60000.0 / avgInterval).toInt().coerceIn(30, 300)
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
        _bpm.intValue = value.coerceIn(30, 300)
        if (engine.running.value) {
            engine.start(_bpm.intValue)
        }
    }

    fun getFinalBpm(): Int {
        val current = _bpm.intValue
        val niceValues = listOf(30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300)
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
                engine.start(_bpm.intValue)
            }
        }
    }

    override fun onCleared() {
        Log.d("MetronomeEngine", "StandaloneVM onCleared")
        bpmSyncJob?.cancel()
        engine.stop()
        super.onCleared()
    }
}