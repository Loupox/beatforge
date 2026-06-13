package com.cheminee.metronome.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cheminee.metronome.data.AppDatabase
import com.cheminee.metronome.data.importer.ParsedSong
import com.cheminee.metronome.data.importer.ParsedSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SetRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: SetRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .fallbackToDestructiveMigration()
            .build()
        repository = SetRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createSet_andObserve_returnsCreatedSet() = runBlocking {
        val setId = repository.createSet("My Set")

        val sets = repository.observeSets().first()
        println("DEBUG SETS BEFORE MOVE = ${sets.map { it.name }}")
        assertEquals(1, sets.size)
        assertEquals("My Set", sets[0].name)
        assertEquals(setId, sets[0].id)
    }

    @Test
    fun createSet_withEmptyName_isIgnored() = runBlocking {
        repository.createSet("")

        val sets = repository.observeSets().first()
        println("DEBUG SETS BEFORE MOVE = ${sets.map { it.name }}")
        assertTrue(sets.isEmpty())
    }

    @Test
    fun createSet_withWhitespaceOnly_isIgnored() = runBlocking {
        repository.createSet("   ")

        val sets = repository.observeSets().first()
        println("DEBUG SETS BEFORE MOVE = ${sets.map { it.name }}")
        assertTrue(sets.isEmpty())
    }

    @Test
    fun renameSet_updatesName() = runBlocking {
        val setId = repository.createSet("Original Name")
        val set = repository.getSet(setId)!!

        repository.renameSet(set, "New Name")

        val updated = repository.getSet(setId)
        assertEquals("New Name", updated?.name)
    }

    @Test
    fun deleteSet_removesFromDatabase() = runBlocking {
        val setId = repository.createSet("To Delete")
        repository.getSet(setId)

        repository.deleteSet(repository.getSet(setId)!!)

        assertNull(repository.getSet(setId))
    }

    @Test
    fun addSong_withValidData_createsSong() = runBlocking {
        val setId = repository.createSet("Set with Songs")

        val songId = repository.addSong(setId, "Test Song", 120)

        val songs = repository.getSongs(setId)
        assertEquals(1, songs.size)
        assertEquals("Test Song", songs[0].name)
        assertEquals(120, songs[0].bpm)
        assertEquals(0, songs[0].position)
    }

    @Test
    fun addSong_multipleSongs_haveIncrementalPositions() = runBlocking {
        val setId = repository.createSet("Set")

        repository.addSong(setId, "Song 1", 100)
        repository.addSong(setId, "Song 2", 110)
        repository.addSong(setId, "Song 3", 120)

        val songs = repository.getSongs(setId)
        assertEquals(3, songs.size)
        assertEquals(0, songs[0].position)
        assertEquals(1, songs[1].position)
        assertEquals(2, songs[2].position)
    }

    @Test
    fun addSong_withEmptyName_isIgnored() = runBlocking {
        val setId = repository.createSet("Set")

        repository.addSong(setId, "", 120)

        val songs = repository.getSongs(setId)
        assertTrue(songs.isEmpty())
    }

    @Test
    fun updateSong_modifiesSong() = runBlocking {
        val setId = repository.createSet("Set")
        val songId = repository.addSong(setId, "Original", 100)
        val song = repository.getSongs(setId)[0]

        repository.updateSong(song.copy(name = "Modified", bpm = 150))

        val updated = repository.getSongs(setId)[0]
        assertEquals("Modified", updated.name)
        assertEquals(150, updated.bpm)
    }

    @Test
    fun deleteSong_removesFromSet() = runBlocking {
        val setId = repository.createSet("Set")
        val songId = repository.addSong(setId, "To Delete", 100)
        val song = repository.getSongs(setId)[0]

        repository.deleteSong(song)

        val songs = repository.getSongs(setId)
        assertTrue(songs.isEmpty())
    }

    @Test
    fun reorderSongs_changesPositions() = runBlocking {
        val setId = repository.createSet("Set")
        repository.addSong(setId, "Song A", 100)
        repository.addSong(setId, "Song B", 110)
        repository.addSong(setId, "Song C", 120)
        val songs = repository.getSongs(setId)

        val reordered = listOf(songs[2], songs[0], songs[1])
        repository.reorder(reordered)

        val result = repository.getSongs(setId)
        assertEquals("Song C", result[0].name)
        assertEquals("Song A", result[1].name)
        assertEquals("Song B", result[2].name)
    }

    @Test
    fun importSet_withValidParsedSet_createsSetAndSongs() = runBlocking {
        val parsedSet = ParsedSet(
            setName = "Imported Set",
            songs = listOf(
                ParsedSong("Song 1", 100, "", 0),
                ParsedSong("Song 2", 120, "", 1)
            )
        )

        val setId = repository.importSet(parsedSet)

        val sets = repository.observeSets().first()
        println("DEBUG SETS BEFORE MOVE = ${sets.map { it.name }}")
        assertEquals(1, sets.size)
        assertEquals("Imported Set", sets[0].name)

        val songs = repository.getSongs(setId)
        assertEquals(2, songs.size)
        assertEquals("Song 1", songs[0].name)
        assertEquals("Song 2", songs[1].name)
    }

    @Test
    fun importSet_withDuplicateTitles_keepsOnlyFirst() = runBlocking {
        val parsedSet = ParsedSet(
            setName = "Set with Duplicates",
            songs = listOf(
                ParsedSong("Same Song", 100, "", 0),
                ParsedSong("Same Song", 110, "", 1),
                ParsedSong("Same Song", 120, "", 2)
            )
        )

        val setId = repository.importSet(parsedSet)

        val songs = repository.getSongs(setId)
        assertEquals(1, songs.size)
        assertEquals(100, songs[0].bpm)
    }

    @Test
    fun moveSet_changesOrder() = runBlocking {
        repository.createSet("Set A")
        repository.createSet("Set B")
        repository.createSet("Set C")
        val sets = repository.observeSets().first()
        System.err.println("DEBUG SETS BEFORE MOVE = ${sets.map { it.name }}")

        repository.moveSet(0, 2, sets)

        val result = repository.observeSets().first()
        assertEquals("Set B", result[0].name)
        assertEquals("Set C", result[1].name)
        assertEquals("Set A", result[2].name)
    }
}