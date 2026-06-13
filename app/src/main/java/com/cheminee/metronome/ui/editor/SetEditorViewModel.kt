package com.cheminee.metronome.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.repository.SetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SetEditorViewModel(private val repository: SetRepository) : ViewModel() {

    private val setIdFlow = MutableStateFlow<Long?>(null)

    val songs: StateFlow<List<Song>> = setIdFlow
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeSongs(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _set = MutableStateFlow<SetList?>(null)
    val set: StateFlow<SetList?> = _set

    private val _recentlyDeletedSong = MutableStateFlow<Song?>(null)
    val recentlyDeletedSong: StateFlow<Song?> = _recentlyDeletedSong

    fun bind(setId: Long) {
        if (setIdFlow.value == setId) return
        setIdFlow.value = setId
        viewModelScope.launch { _set.value = repository.getSet(setId) }
    }

    fun addSong(name: String, bpm: Int, comments: String = "") {
        val id = setIdFlow.value ?: return
        val cleanName = name.trim().ifEmpty { return }
        val cleanBpm = bpm.coerceIn(30, 300)
        viewModelScope.launch { repository.addSong(id, cleanName, cleanBpm, comments) }
    }

    fun updateSong(song: Song, name: String, bpm: Int, comments: String = "") {
        val cleanName = name.trim().ifEmpty { return }
        val cleanBpm = bpm.coerceIn(30, 300)
        viewModelScope.launch {
            repository.updateSong(song.copy(name = cleanName, bpm = cleanBpm, comments = comments))
        }
    }

    fun updateSongComments(song: Song, comments: String) {
        viewModelScope.launch { repository.updateSongComments(song, comments) }
    }

    fun deleteSong(song: Song) {
        _recentlyDeletedSong.value = song
        viewModelScope.launch { repository.deleteSong(song) }
    }

    fun undoDeleteSong() {
        val deleted = _recentlyDeletedSong.value ?: return
        _recentlyDeletedSong.value = null
        viewModelScope.launch {
            repository.addSong(deleted.setId, deleted.name, deleted.bpm, deleted.comments)
        }
    }

    fun move(from: Int, to: Int) {
        val current = songs.value.toMutableList()
        if (from !in current.indices || to !in current.indices) return
        val moved = current.removeAt(from)
        current.add(to, moved)
        viewModelScope.launch { repository.reorder(current) }
    }
}
