package com.cheminee.metronome.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cheminee.metronome.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemineeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onImportClick: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier,
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
            if (onImportClick != null) {
                IconButton(onClick = onImportClick) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = "Importer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemineeSmallTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onImportClick: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    androidx.compose.material3.SmallTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        modifier = modifier,
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
            if (onImportClick != null) {
                IconButton(onClick = onImportClick) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = "Importer"
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}