package com.cheminee.metronome.data.exporter

import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.Song

object JsonExporter {

    fun toJson(set: SetList, songs: List<Song>): String {
        val songsArray = songs
            .sortedBy { it.position }
            .joinToString(",\n") { song ->
                val title = song.name.escapedForJson()
                val comments = song.comments.escapedForJson()
                """  { "title": "$title", "bpm": ${song.bpm}, "comments": "$comments" }"""
            }

        val setName = set.name.escapedForJson()

        return """{
  "setName": "$setName",
  "songs": [
$songsArray
  ]
}"""
    }

    private fun String.escapedForJson(): String {
        return this
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}