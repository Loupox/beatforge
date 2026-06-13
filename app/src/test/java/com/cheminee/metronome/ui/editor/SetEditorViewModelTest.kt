package com.cheminee.metronome.ui.editor

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheminee.metronome.data.AppDatabase
import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.repository.SetRepository
import android.os.Looper
import org.robolectric.Shadows.shadowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)

@OptIn(ExperimentalCoroutinesApi::class)
class SetEditorViewModelTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: SetRepository
    private lateinit var viewModel: SetEditorViewModel
    private val testDispatcher = StandardTestDispatcher()
    private var testSetId: Long = 0

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
        viewModel = SetEditorViewModel(repository)

        runTest {
            testSetId = repository.createSet("Test Set")
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun bind_withValidSetId_loadsSetAndSongs() = runTest {
        repository.addSong(testSetId, "Song 1", 100)
        repository.addSong(testSetId, "Song 2", 120)
        runUntilIdle()

        viewModel.bind(testSetId)
        runUntilIdle()

        val set = viewModel.set.first { it != null }
        assertNotNull(set)
        assertEquals("Test Set", set?.name)

        val songs = viewModel.songs.first { it.size == 2 }
        assertEquals(2, songs.size)
    }

    @Test
    fun addSong_withValidData_addsSongToList() = runTest {
        viewModel.bind(testSetId)
        runUntilIdle()

        viewModel.addSong("New Song", 140)
        runUntilIdle()

        val songs = viewModel.songs.first { it.size == 1 }
        assertEquals(1, songs.size)
        assertEquals("New Song", songs[0].name)
        assertEquals(140, songs[0].bpm)
    }

    @Test
    fun addSong_withEmptyName_ignores() = runTest {
        viewModel.bind(testSetId)
        runUntilIdle()

        viewModel.addSong("", 120)
        runUntilIdle()

        val songs = viewModel.songs.first()
        assertTrue(songs.isEmpty())
    }

    @Test
    fun addSong_withBpmOutOfRange_coercesBpm() = runTest {
        viewModel.bind(testSetId)
        runUntilIdle()

        viewModel.addSong("Song", 10)
        runUntilIdle()

        val song = viewModel.songs.first { it.isNotEmpty() }[0]
        assertEquals(30, song.bpm)
    }

    @Test
    fun addSong_withBpmTooHigh_coercesBpm() = runTest {
        viewModel.bind(testSetId)
        runUntilIdle()

        viewModel.addSong("Song", 500)
        runUntilIdle()

        val song = viewModel.songs.first { it.isNotEmpty() }[0]
        assertEquals(300, song.bpm)
    }

    @Test
    fun updateSong_modifiesExistingSong() = runTest {
        viewModel.bind(testSetId)
        viewModel.addSong("Original", 100)
        runUntilIdle()
        val song = viewModel.songs.first { it.isNotEmpty() }[0]

        viewModel.updateSong(song, "Modified", 150, "Comment")
        runUntilIdle()

        val updated = viewModel.songs.drop(1).first { it.isNotEmpty() }[0]
        assertEquals("Modified", updated.name)
        assertEquals(150, updated.bpm)
        assertEquals("Comment", updated.comments)
    }

    @Test
    fun updateSong_withEmptyName_ignores() = runTest {
        viewModel.bind(testSetId)
        viewModel.addSong("Original", 100)
        runUntilIdle()
        val song = viewModel.songs.first { it.isNotEmpty() }[0]

        viewModel.updateSong(song, "", 120)
        runUntilIdle()

        val unchanged = viewModel.songs.first { it.isNotEmpty() }[0]
        assertEquals("Original", unchanged.name)
    }

    @Test
    fun deleteSong_removesFromList() = runTest {
        viewModel.bind(testSetId)
        viewModel.addSong("To Delete", 100)
        runUntilIdle()
        val song = viewModel.songs.drop(1).first { it.isNotEmpty() }[0]

        viewModel.deleteSong(song)
        runUntilIdle()

        val songs = viewModel.songs.drop(1).first { it.isEmpty() }
        assertTrue(songs.isEmpty())
    }

@Test
    fun move_withValidIndices_reordersSongs() = runTest {
        repository.addSong(testSetId, "A", 100)
        repository.addSong(testSetId, "B", 110)
        repository.addSong(testSetId, "C", 120)

        viewModel.bind(testSetId)
        runUntilIdle()

        val initialSongs = viewModel.songs.first { it.size == 3 }
        assertEquals(listOf("A", "B", "C"), initialSongs.map { it.name })

        viewModel.move(0, 2)
        val reorderedSongs = viewModel.songs.drop(1).first { it.size == 3 }
        assertEquals(listOf("B", "C", "A"), reorderedSongs.map { it.name })
    }

    @Test
    fun move_withInvalidIndices_doesNothing() = runTest {
        viewModel.bind(testSetId)
        viewModel.addSong("A", 100)
        runUntilIdle()

        viewModel.move(0, 5)
        runUntilIdle()

        val songs = viewModel.songs.first { it.size == 1 }
        assertEquals("A", songs[0].name)
    }

    @Test
    fun bind_withSameSetId_doesNotReload() = runTest {
        viewModel.bind(testSetId)
        runUntilIdle()

        repository.addSong(testSetId, "Song", 100)
        runUntilIdle()

        viewModel.bind(testSetId)
        runUntilIdle()

        val songs = viewModel.songs.first { it.size == 1 }
        assertEquals(1, songs.size)
    }
}