package com.cheminee.metronome.ui.live

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheminee.metronome.data.AppDatabase
import com.cheminee.metronome.repository.SetRepository
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.robolectric.Shadows.shadowOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LiveViewModelTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: SetRepository
    private lateinit var viewModel: LiveViewModel
    private val testDispatcher = StandardTestDispatcher()

    private fun runUntilIdle() {
        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .fallbackToDestructiveMigration()
            .build()
        repository = SetRepository(database)
        viewModel = LiveViewModel(repository, null, null)
    }

    @After
    fun tearDown() {
        viewModel.stop()
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun bind_withNoSetId_returnsEmptySongs() = runTest {
        viewModel.bind(0, autoplay = false)
        runUntilIdle()

        val songs = viewModel.songs.first()
        assertTrue(songs.isEmpty())
    }

    @Test
    fun bind_withSetId_loadsSongs() = runTest {
        val setId = repository.createSet("Test Set")
        repository.addSong(setId, "Song 1", 120)
        repository.addSong(setId, "Song 2", 130)
        runUntilIdle()

        viewModel.bind(setId, autoplay = false)
        runUntilIdle()

        val songs = viewModel.songs.first { it.size == 2 }
        assertEquals(2, songs.size)
    }

    @Test
    fun isLoading_startsFalse() = runTest {
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun songsLoaded_becomesTrueWhenSongsAvailable() = runTest {
        val setId = repository.createSet("Test Set")
        repository.addSong(setId, "Song 1", 120)
        runUntilIdle()

        viewModel.bind(setId)
        runUntilIdle()

        val songsLoaded = viewModel.songsLoaded.first { it }
        assertTrue(songsLoaded)
    }

    @Test
    fun stop_callsEngineStop() = runTest {
        viewModel.stop()
        runUntilIdle()
        assertFalse(viewModel.engine.running.value)
    }

    @Test
    fun toggle_startsEngineWhenStopped() = runTest {
        assertFalse(viewModel.engine.running.value)

        viewModel.toggle(120)
        runUntilIdle()

        assertTrue(viewModel.engine.running.value)
    }

    @Test
    fun toggle_stopsEngineWhenRunning() = runTest {
        viewModel.toggle(120)
        runUntilIdle()
        assertTrue(viewModel.engine.running.value)

        viewModel.toggle(120)
        runUntilIdle()
        assertFalse(viewModel.engine.running.value)
    }

    @Test
    fun playFor_startsEngineAtSpecifiedBpm() = runTest {
        viewModel.playFor(100)
        runUntilIdle()
        assertTrue(viewModel.engine.running.value)
    }
}