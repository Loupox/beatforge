package com.cheminee.metronome.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SetListDao {
    @Query("SELECT * FROM set_list ORDER BY position ASC, id ASC")
    fun observeAll(): Flow<List<SetList>>

    @Query("SELECT COUNT(*) FROM set_list")
    suspend fun countAll(): Int

    @Query("SELECT * FROM set_list WHERE id = :id")
    suspend fun getById(id: Long): SetList?

    @Query("UPDATE set_list SET songCount = songCount + 1 WHERE id = :setId")
    suspend fun incrementSongCount(setId: Long)

    @Query("UPDATE set_list SET songCount = songCount - 1 WHERE id = :setId AND songCount > 0")
    suspend fun decrementSongCount(setId: Long)

    @Query("UPDATE set_list SET songCount = :count WHERE id = :setId")
    suspend fun setSongCount(setId: Long, count: Int)

    @Insert
    suspend fun insert(set: SetList): Long

    @Update
    suspend fun update(set: SetList)

    @Update
    suspend fun updateAll(sets: List<SetList>)

    @Delete
    suspend fun delete(set: SetList)
}
