package com.cheminee.metronome.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "song",
    foreignKeys = [
        ForeignKey(
            entity = SetList::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("setId")]
)
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val setId: Long,
    val name: String,
    val bpm: Int,
    val position: Int,
    val comments: String = ""
)
