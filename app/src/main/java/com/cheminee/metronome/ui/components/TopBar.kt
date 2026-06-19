package com.cheminee.metronome.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cheminee.metronome.ui.theme.BeatForgeTextStyles
import com.cheminee.metronome.ui.theme.BorderThickness

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemineeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = BeatForgeTextStyles.screenTitle
            )
        },
        modifier = modifier.statusBarsPadding()
            .border(BorderStroke(BorderThickness.thin, MaterialTheme.colorScheme.outline)),
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
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
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = BeatForgeTextStyles.screenTitle
            )
        },
        modifier = modifier.statusBarsPadding()
            .border(BorderStroke(BorderThickness.thin, MaterialTheme.colorScheme.outline)),
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}