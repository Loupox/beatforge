package com.cheminee.metronome.data.importer

import org.junit.Assert.*
import org.junit.Test

class JsonImportParserTest {
    @Test
    fun parse_readme_example_returnsExpected() {
        val json = """
        {
          "setName": "Set 1",
          "songs": [
            { "title": "Billie Jean (Michael Jackson)", "bpm": 117, "comments": "" },
            { "title": "Give Me the Night (George Benson)", "bpm": 110, "comments": "" }
          ]
        }
        """.trimIndent()

        val parsed = JsonImportParser.parse(json)
        assertEquals("Set 1", parsed.setName)
        assertEquals(2, parsed.songs.size)
        assertEquals("Billie Jean (Michael Jackson)", parsed.songs[0].title)
        assertEquals(117, parsed.songs[0].bpm)
    }
}
