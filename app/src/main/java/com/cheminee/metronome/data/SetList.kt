package com.cheminee.metronome.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "set_list")
data class SetList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val position: Int = 0,
    val songCount: Int = 0
)
