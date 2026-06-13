package com.cheminee.metronome.ui.sets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.importer.ParsedSet
import com.cheminee.metronome.repository.SetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SetsListViewModel(private val repository: SetRepository) : ViewModel() {

    val sets: StateFlow<List<SetList>> = repository.observeSets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _recentlyDeletedSet = MutableStateFlow<SetList?>(null)
    val recentlyDeletedSet: StateFlow<SetList?> = _recentlyDeletedSet

    fun createSet(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.createSet(trimmed) }
    }

    fun importSet(parsedSet: ParsedSet, onResult: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            try {
                val id = repository.importSet(parsedSet)
                onResult(Result.success(id))
            } catch (error: Throwable) {
                onResult(Result.failure(error))
            }
        }
    }

    fun renameSet(set: SetList, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.renameSet(set, trimmed) }
    }

    fun deleteSet(set: SetList) {
        _recentlyDeletedSet.value = set
        viewModelScope.launch { repository.deleteSet(set) }
    }

    fun undoDeleteSet() {
        val deleted = _recentlyDeletedSet.value ?: return
        _recentlyDeletedSet.value = null
        viewModelScope.launch { repository.createSet(deleted.name) }
    }

    fun moveSet(from: Int, to: Int) {
        val current = sets.value.toMutableList()
        if (from !in current.indices || to !in current.indices) return
        val moved = current.removeAt(from)
        current.add(to, moved)
        viewModelScope.launch { repository.reorderSets(current) }
    }

    suspend fun exportSet(setId: Long): String? {
        return repository.exportSet(setId)
    }
}
