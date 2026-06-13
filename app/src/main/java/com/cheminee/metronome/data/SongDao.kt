package com.cheminee.metronome.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song WHERE setId = :setId ORDER BY position ASC, id ASC")
    fun observeBySet(setId: Long): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE setId = :setId ORDER BY position ASC, id ASC")
    suspend fun getBySet(setId: Long): List<Song>

    @Query("SELECT COUNT(*) FROM song WHERE setId = :setId")
    suspend fun countForSet(setId: Long): Int

    @Insert
    suspend fun insert(song: Song): Long

    @Update
    suspend fun update(song: Song)

    @Update
    suspend fun updateAll(songs: List<Song>)

    @Delete
    suspend fun delete(song: Song)
}
