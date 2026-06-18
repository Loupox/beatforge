package com.cheminee.metronome.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.cheminee.metronome.R

@Composable
fun AppTopBarMenu(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onImportClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = {
        expanded = !expanded
    }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        MenuItem(
            icon = Icons.Default.Info,
            label = stringResource(R.string.menu_about),
            onClick = {
                expanded = false
                onNavigateToAbout()
            }
        )
        MenuItem(
            icon = Icons.Default.Settings,
            label = stringResource(R.string.menu_settings),
            onClick = {
                expanded = false
                onNavigateToSettings()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.import_action)) },
            leadingIcon = { Icon(Icons.Default.FileUpload, contentDescription = null) },
            onClick = {
                expanded = false
                onImportClick()
            }
        )
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        onClick = onClick
    )
}