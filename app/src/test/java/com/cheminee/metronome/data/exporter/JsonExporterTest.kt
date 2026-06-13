package com.cheminee.metronome.data.exporter

import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.Song
import org.junit.Assert.*
import org.junit.Test

class JsonExporterTest {
    @Test
    fun toJson_outputsExpectedJsonStructure() {
        val set = SetList(id = 1, name = "My Set")
        val songs = listOf(
            Song(id = 1, setId = 1, name = "Song One", bpm = 120, position = 0, comments = ""),
            Song(id = 2, setId = 1, name = "Song Two", bpm = 130, position = 1, comments = "Test")
        )

        val json = JsonExporter.toJson(set, songs)

        assertTrue(json.contains("\"setName\": \"My Set\""))
        assertTrue(json.contains("\"title\": \"Song One\""))
        assertTrue(json.contains("\"bpm\": 130"))
        assertTrue(json.contains("\"comments\": \"Test\""))
        assertTrue(json.indexOf("Song One") < json.indexOf("Song Two"))
    }

    @Test
    fun toJson_escapesQuotesAndBackslashes() {
        val set = SetList(id = 1, name = "My \"Set\"")
        val songs = listOf(
            Song(id = 1, setId = 1, name = "Line\nBreak", bpm = 100, position = 0, comments = "Quote \" Test")
        )

        val json = JsonExporter.toJson(set, songs)

        assertTrue(json.contains("My \\\"Set\\\""))
        assertTrue(json.contains("Line\\nBreak"))
        assertTrue(json.contains("Quote \\\" Test"))
    }
}
