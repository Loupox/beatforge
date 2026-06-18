package com.cheminee.metronome.ui.live

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.data.TimeSignature
import com.cheminee.metronome.metronome.MetronomeEngine
import com.cheminee.metronome.repository.SetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
class LiveViewModel(
    private val repository: SetRepository,
    private val preferencesManager: PreferencesManager? = null,
    @Suppress("UNUSED_PARAMETER") private val context: Context? = null
) : ViewModel() {

    private val setIdFlow = MutableStateFlow<Long?>(null)
    private val autoplayRequested = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _songsLoaded = MutableStateFlow(false)
    val songsLoaded: StateFlow<Boolean> = _songsLoaded

    val songs: StateFlow<List<Song>> = setIdFlow
        .flatMapLatest { id ->
            _isLoading.value = true
            _songsLoaded.value = false
            if (id == null) {
                _isLoading.value = false
                flowOf(emptyList())
            } else {
                repository.observeSongs(id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val engine: MetronomeEngine = MetronomeEngine

    val flashEnabled = preferencesManager?.flashEnabled
    val flashColorIndex = preferencesManager?.flashColorIndex
    val soundEnabled = preferencesManager?.soundEnabled
    val vibrationEnabled = preferencesManager?.vibrationEnabled
    val accentFirstBeatEnabled = preferencesManager?.accentFirstBeatEnabled
    val timeSignature = preferencesManager?.timeSignature

    fun toggleVibration() {
        val current = preferencesManager?.vibrationEnabled?.value ?: false
        preferencesManager?.setVibrationEnabled(!current)
    }

    private val playForMutex = Mutex()

    init {
        engine.setScope(viewModelScope)

        viewModelScope.launch {
            songs.collect { songsList ->
                Log.d("MetronomeEngine", "LiveVM songs.collect: list size = ${songsList.size}, autoplayRequested = ${autoplayRequested.value}")
                _isLoading.value = false
                if (songsList.isNotEmpty()) {
                    _songsLoaded.value = true
                }

                if (autoplayRequested.value && songsList.isNotEmpty()) {
                    songsList.firstOrNull()?.let { song ->
                        Log.d("MetronomeEngine", "LiveVM songs.collect: autoplaying song ${song.name} at ${song.bpm} BPM")
                        engine.start(song.bpm)
                    }
                }
            }
        }
    }

    fun bind(setId: Long, autoplay: Boolean = false) {
        Log.d("MetronomeEngine", "LiveVM bind: setId = $setId, autoplay = $autoplay")
        _isLoading.value = true
        _songsLoaded.value = false
        setIdFlow.value = setId
        autoplayRequested.value = autoplay
    }

    fun toggle(bpm: Int) {
        Log.d("MetronomeEngine", "LiveVM toggle: bpm = $bpm, running = ${engine.running.value}")
        if (engine.running.value) {
            engine.stop()
        } else {
            engine.start(bpm)
        }
    }

    fun playFor(bpm: Int) {
        viewModelScope.launch {
            playForMutex.withLock {
                if (engine.running.value && engine.currentBpm.value == bpm) {
                    Log.d("MetronomeEngine", "LiveVM playFor: already running at $bpm, skipping")
                    return@withLock
                }
                Log.d("MetronomeEngine", "LiveVM playFor: bpm = $bpm, currentBpm = ${engine.currentBpm.value}")
                engine.start(bpm)
            }
        }
    }

    fun stop() {
        Log.d("MetronomeEngine", "LiveVM stop called")
        engine.stop()
    }

    fun toggleSound() {
        val current = preferencesManager?.soundEnabled?.value ?: true
        preferencesManager?.setSoundEnabled(!current)
    }

    fun setFlashColorIndex(index: Int) {
        preferencesManager?.setFlashColorIndex(index)
    }

    override fun onCleared() {
        Log.d("MetronomeEngine", "LiveVM onCleared: stopping engine")
        engine.stop()
        super.onCleared()
    }
}