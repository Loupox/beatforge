package com.cheminee.metronome.repository

import android.util.Log
import androidx.room.withTransaction
import com.cheminee.metronome.data.AppDatabase
import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.data.importer.JsonImportParser
import com.cheminee.metronome.data.importer.ParsedSet
import com.cheminee.metronome.data.importer.ParsedSong
import com.cheminee.metronome.data.exporter.JsonExporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class SetRepository(private val database: AppDatabase) {
    private val setListDao = database.setListDao()
    private val songDao = database.songDao()

    fun observeSets(): Flow<List<SetList>> = setListDao.observeAll()

    fun observeSongs(setId: Long): Flow<List<Song>> {
        Log.d("MetronomeEngine", "Repository observeSongs: setId=$setId")
        return songDao.observeBySet(setId).onEach { songs ->
            Log.d("MetronomeEngine", "Repository observeSongs: received ${songs.size} songs for setId=$setId")
        }
    }

    suspend fun getSet(id: Long): SetList? = setListDao.getById(id)

    suspend fun getSongs(setId: Long): List<Song> = songDao.getBySet(setId)

    suspend fun createSet(name: String): Long {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return -1
        val position = setListDao.countAll()
        return setListDao.insert(SetList(name = trimmedName, position = position))
    }

    suspend fun renameSet(set: SetList, newName: String) =
        setListDao.update(set.copy(name = newName.trim()))

    suspend fun deleteSet(set: SetList) = setListDao.delete(set)

    suspend fun addSong(setId: Long, name: String, bpm: Int, comments: String = ""): Long {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return -1
        val position = songDao.countForSet(setId)
        val id = songDao.insert(Song(setId = setId, name = trimmedName, bpm = bpm, position = position, comments = comments))
        setListDao.incrementSongCount(setId)
        return id
    }

    suspend fun updateSong(song: Song) = songDao.update(song)

    suspend fun updateSongComments(song: Song, comments: String) =
        songDao.update(song.copy(comments = comments))

    suspend fun deleteSong(song: Song) {
        songDao.delete(song)
        setListDao.decrementSongCount(song.setId)
    }

    suspend fun importSet(parsedSet: ParsedSet): Long {
        return database.withTransaction {
            val uniqueSongs = mergeByNormalizedTitle(parsedSet.songs)
            val position = setListDao.countAll()
            val setId = setListDao.insert(
                SetList(name = parsedSet.setName, songCount = uniqueSongs.size, position = position)
            )
            uniqueSongs.forEach { parsedSong ->
                songDao.insert(
                    Song(
                        setId = setId,
                        name = parsedSong.title,
                        bpm = parsedSong.bpm,
                        position = parsedSong.position,
                        comments = parsedSong.comments
                    )
                )
            }
            setId
        }
    }

    private fun mergeByNormalizedTitle(songs: List<ParsedSong>): List<ParsedSong> {
        val unique = linkedMapOf<String, ParsedSong>()
        songs.forEach { song ->
            val normalized = JsonImportParser.normalizedTitle(song.title)
            if (normalized !in unique) {
                unique[normalized] = song
            }
        }
        return unique.values.toList()
    }

    suspend fun reorder(songs: List<Song>) {
        val renumbered = songs.mapIndexed { index, song -> song.copy(position = index) }
        songDao.updateAll(renumbered)
    }

    suspend fun reorderSets(sets: List<SetList>) {
        val renumbered = sets.mapIndexed { index, set -> set.copy(position = index) }
        setListDao.updateAll(renumbered)
    }

    suspend fun moveSet(from: Int, to: Int, sets: List<SetList>) {
        val current = sets.toMutableList()
        if (from !in current.indices || to !in current.indices) return
        val moved = current.removeAt(from)
        current.add(to, moved)
        reorderSets(current)
    }

    suspend fun exportSet(setId: Long): String? {
        val set = getSet(setId) ?: return null
        val songs = getSongs(setId)
        return JsonExporter.toJson(set, songs)
    }
}
