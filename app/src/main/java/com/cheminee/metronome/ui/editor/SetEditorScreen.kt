package com.cheminee.metronome.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cheminee.metronome.R
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.ui.components.ChemineeCard
import com.cheminee.metronome.ui.components.ChemineeSmallTopBar
import com.cheminee.metronome.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetEditorScreen(
    setId: Long,
    viewModel: SetEditorViewModel,
    onBack: () -> Unit,
    onLaunchLive: () -> Unit
) {
    LaunchedEffect(setId) { viewModel.bind(setId) }
    val songs by viewModel.songs.collectAsState()
    val set by viewModel.set.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Song?>(null) }
    var deletingSong by remember { mutableStateOf<Song?>(null) }

    Scaffold(
        topBar = {
            ChemineeSmallTopBar(
                title = set?.name ?: "",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLaunchLive, enabled = songs.isNotEmpty()) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.launch_live),
                            tint = if (songs.isNotEmpty())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_song),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        if (songs.isEmpty()) {
            EmptyState(padding)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items(songs, key = { it.id }) { song ->
                    val index = songs.indexOf(song)
                    SongCard(
                        song = song,
                        canMoveUp = index > 0,
                        canMoveDown = index < songs.lastIndex,
                        onEdit = { editing = song },
                        onDeleteRequest = { deletingSong = song },
                        onMoveUp = { viewModel.move(index, index - 1) },
                        onMoveDown = { viewModel.move(index, index + 1) }
                    )
                }
            }
        }
    }

    if (showAdd) {
        SongDialog(
            title = stringResource(R.string.add_song),
            initialName = "",
            initialBpm = "120",
            initialComments = "",
            onConfirm = { name, bpm, comments ->
                viewModel.addSong(name, bpm, comments)
                showAdd = false
            },
            onDismiss = { showAdd = false }
        )
    }

    editing?.let { song ->
        SongDialog(
            title = "Modifier",
            initialName = song.name,
            initialBpm = song.bpm.toString(),
            initialComments = song.comments,
            onConfirm = { name, bpm, comments ->
                viewModel.updateSong(song, name, bpm, comments)
                editing = null
            },
            onDismiss = { editing = null }
        )
    }

    deletingSong?.let { song ->
        AlertDialog(
            onDismissRequest = { deletingSong = null },
            title = { Text(stringResource(R.string.confirm_delete_song_title)) },
            text = { Text(stringResource(R.string.confirm_delete_song_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSong(song)
                    deletingSong = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSong = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SongCard(
    song: Song,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    ChemineeCard(
        title = song.name,
        subtitle = "${song.bpm} BPM"
    ) {
        Column {
            if (song.comments.isNotBlank()) {
                Text(
                    text = song.comments,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = Spacing.xs)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                    Icon(
                        Icons.Filled.ArrowUpward,
                        contentDescription = null,
                        tint = if (canMoveUp)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                    Icon(
                        Icons.Filled.ArrowDownward,
                        contentDescription = null,
                        tint = if (canMoveDown)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                TextButton(onClick = onEdit) {
                    Text(
                        stringResource(R.string.edit),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteRequest) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun SongDialog(
    title: String,
    initialName: String,
    initialBpm: String,
    initialComments: String,
    onConfirm: (name: String, bpm: Int, comments: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var bpmText by remember { mutableStateOf(initialBpm) }
    var comments by remember { mutableStateOf(initialComments) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.song_name_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                OutlinedTextField(
                    value = bpmText,
                    onValueChange = { v -> bpmText = v.filter { it.isDigit() }.take(3) },
                    singleLine = true,
                    label = { Text(stringResource(R.string.bpm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text(stringResource(R.string.song_comments)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val bpm = bpmText.toIntOrNull() ?: 120
                onConfirm(name, bpm, comments)
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun EmptyState(padding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.empty_songs))
    }
}