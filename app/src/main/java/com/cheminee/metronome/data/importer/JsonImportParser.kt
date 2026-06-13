package com.cheminee.metronome.data.importer

import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.Song
import org.json.JSONArray
import org.json.JSONObject

data class ParsedSong(val title: String, val bpm: Int, val comments: String, val position: Int)
data class ParsedSet(val setName: String, val songs: List<ParsedSong>)
data class ImportPreview(
    val parsedSet: ParsedSet,
    val duplicateTitles: List<String>,
    val totalSongs: Int,
    val uniqueSongs: Int
)

object JsonImportParser {
    /**
     * Parse a JSON string following the README example and return a ParsedSet.
     * BPM nullable in JSON will be mapped to 0.
     */
    fun parse(json: String): ParsedSet {
        val root = JSONObject(json)
        val setName = root.optString("setName", "Imported Set")
        val songsArray = root.optJSONArray("songs") ?: JSONArray()
        val songs = mutableListOf<ParsedSong>()
        for (i in 0 until songsArray.length()) {
            val o = songsArray.getJSONObject(i)
            val title = o.optString("title", "Untitled")
            val bpm = if (o.isNull("bpm")) 0 else o.optInt("bpm", 0)
            val comments = o.optString("comments", "")
            songs.add(ParsedSong(title = title, bpm = bpm, comments = comments, position = i))
        }
        return ParsedSet(setName = setName, songs = songs)
    }

    fun buildPreview(parsedSet: ParsedSet): ImportPreview {
        val seen = linkedSetOf<String>()
        val duplicates = mutableListOf<String>()
        parsedSet.songs.forEach { song ->
            val normalized = normalizedTitle(song.title)
            if (!seen.add(normalized)) {
                duplicates.add(song.title)
            }
        }
        return ImportPreview(
            parsedSet = parsedSet,
            duplicateTitles = duplicates,
            totalSongs = parsedSet.songs.size,
            uniqueSongs = seen.size
        )
    }

    fun normalizedTitle(title: String): String {
        return title
            .replace(Regex("""\s*[\(\[].*?[\)\]]\s*"""), "")
            .trim()
            .lowercase()
    }

    /** Convert ParsedSet to Room entities (not inserting DB here). */
    fun toEntities(parsed: ParsedSet, setId: Long = 0): Pair<SetList, List<Song>> {
        val set = SetList(id = setId, name = parsed.setName)
        val songs = parsed.songs.map { ps ->
            Song(
                id = 0,
                setId = setId,
                name = ps.title,
                bpm = ps.bpm,
                position = ps.position,
                comments = ps.comments
            )
        }
        return Pair(set, songs)
    }
}
