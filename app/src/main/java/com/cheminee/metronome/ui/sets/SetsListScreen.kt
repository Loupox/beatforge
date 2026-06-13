package com.cheminee.metronome.ui.sets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cheminee.metronome.R
import com.cheminee.metronome.data.SetList
import com.cheminee.metronome.data.importer.ImportPreview
import com.cheminee.metronome.data.importer.JsonImportParser
import com.cheminee.metronome.ui.common.NameDialog
import com.cheminee.metronome.ui.components.ChemineeCard
import com.cheminee.metronome.ui.components.ChemineeSmallTopBar
import com.cheminee.metronome.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetsListScreen(
    viewModel: SetsListViewModel,
    onOpenSet: (Long) -> Unit,
    onLaunchLive: (Long, Boolean) -> Unit,
    showTopBar: Boolean = true,
    triggerImport: Boolean = false,
    onImportHandled: () -> Unit = {}
) {
    val sets by viewModel.sets.collectAsState()
    var showCreate by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf<SetList?>(null) }
    var deletingSet by remember { mutableStateOf<SetList?>(null) }
    var importPreview by remember { mutableStateOf<ImportPreview?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var exportingSetId by remember { mutableStateOf<Long?>(null) }
    var exportingSetName by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var pendingImport by remember { mutableStateOf(false) }

    LaunchedEffect(triggerImport) {
        if (triggerImport) {
            pendingImport = true
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            try {
                val text = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()
                    .use { it?.readText() }
                    ?: ""
                val parsed = JsonImportParser.parse(text)
                importPreview = JsonImportParser.buildPreview(parsed)
            } catch (throwable: Throwable) {
                errorMessage = throwable.message ?: "Erreur d'import"
            }
        }
    )

    LaunchedEffect(pendingImport) {
        if (pendingImport) {
            importLauncher.launch(arrayOf("application/json"))
            pendingImport = false
            onImportHandled()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            if (uri == null) {
                exportingSetId = null
                exportingSetName = null
                return@rememberLauncherForActivityResult
            }
            coroutineScope.launch {
                try {
                    val json = viewModel.exportSet(exportingSetId!!)
                    if (json != null) {
                        context.contentResolver.openOutputStream(uri)?.use { output ->
                            output.write(json.toByteArray())
                        }
                        snackbarHostState.showSnackbar(context.getString(R.string.export_set_success))
                    } else {
                        snackbarHostState.showSnackbar(context.getString(R.string.export_set_error))
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar(context.getString(R.string.export_set_error))
                }
                exportingSetId = null
                exportingSetName = null
            }
        }
    )

    LaunchedEffect(exportingSetId, exportingSetName) {
        if (exportingSetId != null && exportingSetName != null) {
            val fileName = "${exportingSetName!!.replace(" ", "_")}.json"
            exportLauncher.launch(fileName)
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                ChemineeSmallTopBar(
                    title = stringResource(R.string.sets_title),
                    onImportClick = { importLauncher.launch(arrayOf("application/json")) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreate = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.new_set),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        if (sets.isEmpty()) {
            EmptyState(padding = padding)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items(sets, key = { it.id }) { set ->
                    SetCard(
                        set = set,
                        onOpen = { onOpenSet(set.id) },
                        onLaunch = { onLaunchLive(set.id, true) },
                        onExport = {
                            exportingSetId = set.id
                            exportingSetName = set.name
                        },
                        onRename = { renaming = set },
                        onDeleteRequest = { deletingSet = set }
                    )
                }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    importPreview?.let { preview ->
        AlertDialog(
            onDismissRequest = { importPreview = null },
            title = { Text("Importer \"${preview.parsedSet.setName}\"") },
            text = {
                Column {
                    Text("${preview.totalSongs} morceaux detectes")
                    Text("${preview.uniqueSongs} titres uniques, ${preview.duplicateTitles.size} doublons ignores")
                    if (preview.duplicateTitles.isNotEmpty()) {
                        Text("Doublons detectes:")
                        preview.duplicateTitles.forEach { duplicate ->
                            Text("• $duplicate")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.importSet(preview.parsedSet) { result ->
                        if (result.isSuccess) {
                            errorMessage = "Import termine"
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Erreur pendant l'import"
                        }
                        importPreview = null
                    }
                }) {
                    Text("Importer")
                }
            },
            dismissButton = {
                TextButton(onClick = { importPreview = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showCreate) {
        NameDialog(
            title = stringResource(R.string.new_set),
            label = stringResource(R.string.set_name_hint),
            initial = "",
            onConfirm = { name ->
                viewModel.createSet(name)
                showCreate = false
            },
            onDismiss = { showCreate = false }
        )
    }

    deletingSet?.let { set ->
        AlertDialog(
            onDismissRequest = { deletingSet = null },
            title = { Text(stringResource(R.string.confirm_delete_set_title)) },
            text = { Text(stringResource(R.string.confirm_delete_set_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSet(set)
                    deletingSet = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSet = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    renaming?.let { set ->
        NameDialog(
            title = stringResource(R.string.rename),
            label = stringResource(R.string.set_name_hint),
            initial = set.name,
            onConfirm = { name ->
                viewModel.renameSet(set, name)
                renaming = null
            },
            onDismiss = { renaming = null }
        )
    }
}

@Composable
private fun SetCard(
    set: SetList,
    onOpen: () -> Unit,
    onLaunch: () -> Unit,
    onExport: () -> Unit,
    onRename: () -> Unit,
    onDeleteRequest: () -> Unit
) {
    ChemineeCard(
        title = set.name,
        trailing = {
            Row {
                if (set.songCount > 0) {
                    IconButton(onClick = onLaunch) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Live",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onExport) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = stringResource(R.string.export),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onOpen) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ouvrir",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRename) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Renommer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
        },
        content = null
    )
}

@Composable
private fun EmptyState(padding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.empty_sets))
        }
    }
}
