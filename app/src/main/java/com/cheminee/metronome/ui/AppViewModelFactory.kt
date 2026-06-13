package com.cheminee.metronome.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.repository.SetRepository
import com.cheminee.metronome.ui.editor.SetEditorViewModel
import com.cheminee.metronome.ui.live.LiveViewModel
import com.cheminee.metronome.ui.metronome.StandaloneMetronomeViewModel
import com.cheminee.metronome.ui.sets.SetsListViewModel

class AppViewModelFactory(
    private val repository: SetRepository,
    private val preferencesManager: PreferencesManager? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(SetsListViewModel::class.java) ->
            SetsListViewModel(repository) as T
        modelClass.isAssignableFrom(SetEditorViewModel::class.java) ->
            SetEditorViewModel(repository) as T
        modelClass.isAssignableFrom(LiveViewModel::class.java) ->
            LiveViewModel(repository, preferencesManager, context) as T
        modelClass.isAssignableFrom(StandaloneMetronomeViewModel::class.java) ->
            StandaloneMetronomeViewModel(preferencesManager, true, context) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}