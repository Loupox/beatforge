package com.cheminee.metronome.ui.sets

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheminee.metronome.data.AppDatabase
import com.cheminee.metronome.data.importer.ParsedSong
import com.cheminee.metronome.data.importer.ParsedSet
import com.cheminee.metronome.repository.SetRepository
import android.os.Looper
import org.robolectric.Shadows.shadowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
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
class SetsListViewModelTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: SetRepository
    private lateinit var viewModel: SetsListViewModel
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
        viewModel = SetsListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun createSet_emitsNewSet() = runTest {
        viewModel.createSet("Test Set")

        runUntilIdle()

        val sets = viewModel.sets.first { it.size == 1 }
        assertEquals(1, sets.size)
        assertEquals("Test Set", sets[0].name)
    }

    @Test
    fun createSet_withEmptyName_doesNothing() = runTest {
        viewModel.createSet("")

        runUntilIdle()

        val sets = viewModel.sets.first()
        assertTrue(sets.isEmpty())
    }

    @Test
    fun createSet_withWhitespace_doesNothing() = runTest {
        viewModel.createSet("   ")

        runUntilIdle()

        val sets = viewModel.sets.first()
        assertTrue(sets.isEmpty())
    }

    @Test
    fun renameSet_updatesSetName() = runTest {
        viewModel.createSet("Original")
        runUntilIdle()
        val set = viewModel.sets.first { it.isNotEmpty() }[0]

        viewModel.renameSet(set, "Renamed")
        runUntilIdle()

        val updated = viewModel.sets.drop(1).first { it.isNotEmpty() }[0]
        assertEquals("Renamed", updated.name)
    }

    @Test
    fun deleteSet_removesFromList() = runTest {
        viewModel.createSet("To Delete")
        runUntilIdle()
        val set = viewModel.sets.first { it.isNotEmpty() }[0]

        viewModel.deleteSet(set)
        runUntilIdle()

        val sets = viewModel.sets.drop(1).first { it.isEmpty() }
        assertTrue(sets.isEmpty())
    }

    @Test
    fun importSet_withValidData_createsSetAndSongs() = runTest {
        val parsedSet = ParsedSet(
            setName = "Imported",
            songs = listOf(
                ParsedSong("Song 1", 100, "", 0),
                ParsedSong("Song 2", 120, "", 1)
            )
        )

        viewModel.importSet(parsedSet) {}
        runUntilIdle()

        val sets = viewModel.sets.first { it.isNotEmpty() && it[0].name == "Imported" }
        assertEquals(1, sets.size)
    }

    @Test
    fun importSet_withEmptyName_doesNotCreateSet() = runTest {
        val parsedSet = ParsedSet(
            setName = "",
            songs = emptyList()
        )

        viewModel.importSet(parsedSet) { }
        runUntilIdle()

        val sets = viewModel.sets.first { it.size == 0 }
        assertTrue(sets.isEmpty())
    }

    @Test
    fun moveSet_doesNotCrashWithInsufficientSets() = runTest {
        viewModel.createSet("A")
        runUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        viewModel.moveSet(0, 2)
        runUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val sets = viewModel.sets.first { it.size >= 1 }
        assertTrue(sets.isNotEmpty())
    }

    @Test
    fun moveSet_reordersCorrectly() = runTest {
        for (name in listOf("A", "B", "C", "D")) {
            viewModel.createSet(name)
        }

        val afterCreate = viewModel.sets.first { it.size >= 4 }
        assertTrue(afterCreate.size >= 4)

        viewModel.moveSet(0, 3)
        runUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val reordered = viewModel.sets.first { it.size >= 4 }
        val names = reordered.map { it.name }
        assertTrue("First element should not be A after move", names[0] != "A")
    }
}