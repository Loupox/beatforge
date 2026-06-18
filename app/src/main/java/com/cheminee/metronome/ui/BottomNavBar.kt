package com.cheminee.metronome.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.cheminee.metronome.R
import com.cheminee.metronome.ui.theme.ChemineeColors

enum class BottomNavItem(
    val route: String?,
    val labelResId: Int,
    val icon: ImageVector
) {
    SETS(Routes.SETS, R.string.nav_sets, Icons.AutoMirrored.Filled.MenuBook),
    METRONOME(Routes.METRONOME, R.string.nav_metronome, Icons.Default.Timer),
    OVERFLOW(null, R.string.nav_more, Icons.Default.MoreVert)
}

@Composable
fun AppBottomNavBar(
    currentRoute: String?,
    onNavigateToSets: () -> Unit,
    onNavigateToMetronome: () -> Unit,
    onOverflowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = when (item) {
                BottomNavItem.SETS -> currentRoute == Routes.SETS
                BottomNavItem.METRONOME -> currentRoute == Routes.METRONOME
                BottomNavItem.OVERFLOW -> false
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
                        BottomNavItem.OVERFLOW -> onOverflowClick()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ChemineeColors.Charbon,
                    selectedTextColor = ChemineeColors.Charbon,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}