package com.cheminee.metronome.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.cheminee.metronome.R

enum class BottomNavItem(
    val route: String?,
    val labelResId: Int,
    val icon: ImageVector
) {
    SETS(Routes.SETS, R.string.nav_sets, Icons.AutoMirrored.Filled.MenuBook),
    METRONOME(Routes.METRONOME, R.string.nav_metronome, Icons.Default.Timer),
    PLUS(null, R.string.nav_more, Icons.Default.Add)
}

@Composable
fun AppBottomNavBar(
    currentRoute: String?,
    onNavigateToSets: () -> Unit,
    onNavigateToMetronome: () -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 0.dp,
        modifier = modifier.border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline))
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = when (item) {
                BottomNavItem.SETS -> currentRoute == Routes.SETS
                BottomNavItem.METRONOME -> currentRoute == Routes.METRONOME
                BottomNavItem.PLUS -> false
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelResId)
                    )
                },
                label = { Text(stringResource(item.labelResId)) },
                selected = selected,
                onClick = {
                    when (item) {
                        BottomNavItem.SETS -> onNavigateToSets()
                        BottomNavItem.METRONOME -> onNavigateToMetronome()
                        BottomNavItem.PLUS -> onPlusClick()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}