package com.cheminee.metronome.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.ui.about.AboutScreen
import com.cheminee.metronome.ui.editor.SetEditorScreen
import com.cheminee.metronome.ui.editor.SetEditorViewModel

import com.cheminee.metronome.ui.live.LivePerformanceScreen
import com.cheminee.metronome.ui.live.LiveViewModel
import com.cheminee.metronome.ui.metronome.MetronomeScreen
import com.cheminee.metronome.ui.metronome.StandaloneMetronomeViewModel
import com.cheminee.metronome.ui.sets.SetsListScreen
import com.cheminee.metronome.ui.sets.SetsListViewModel
import com.cheminee.metronome.ui.settings.SettingsScreen
import com.cheminee.metronome.ui.settings.SettingsViewModel

object Routes {
    const val HOME = "home"
    const val SETS = "sets"
    const val EDITOR = "editor/{setId}"
    const val LIVE = "live/{setId}"
    const val LIVE_AUTOPLAY = "live/{setId}/autoplay"
    const val METRONOME = "metronome"
    const val ABOUT = "about"
    const val SETTINGS = "settings"
    fun editor(setId: Long) = "editor/$setId"
    fun live(setId: Long, autoplay: Boolean = false) = if (autoplay) "live/$setId/autoplay" else "live/$setId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    preferencesManager: PreferencesManager,
    modifier: Modifier = Modifier,
    triggerImport: Boolean = false,
    onImportHandled: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = Routes.SETS, modifier = modifier) {
        composable(Routes.SETS) {
            Log.d("MetronomeEngine", "NavGraph: Entering SETS")
            val vm: SetsListViewModel = viewModel(factory = viewModelFactory)
            SetsListScreen(
                viewModel = vm,
                onOpenSet = { setId ->
                    Log.d("MetronomeEngine", "NavGraph: Navigating to editor: $setId")
                    navController.navigate(Routes.editor(setId))
                },
                onLaunchLive = { setId, autoplay ->
                    Log.d("MetronomeEngine", "NavGraph: Navigating to live: setId=$setId, autoplay=$autoplay")
                    navController.navigate(Routes.live(setId, autoplay))
                },
                showTopBar = false,
                triggerImport = triggerImport,
                onImportHandled = onImportHandled
            )
        }
        composable(
            Routes.EDITOR,
            arguments = listOf(navArgument("setId") { type = NavType.LongType })
        ) { entry ->
            val setId = entry.arguments?.getLong("setId") ?: return@composable
            Log.d("MetronomeEngine", "NavGraph: Entering EDITOR: setId=$setId")
            val vm: SetEditorViewModel = viewModel(factory = viewModelFactory)
            SetEditorScreen(
                setId = setId,
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onLaunchLive = {
                    Log.d("MetronomeEngine", "NavGraph: Navigating from editor to live: setId=$setId")
                    navController.navigate(Routes.live(setId))
                }
            )
        }
        composable(
            Routes.LIVE_AUTOPLAY,
            arguments = listOf(navArgument("setId") { type = NavType.LongType })
        ) { entry ->
            val setId = entry.arguments?.getLong("setId") ?: return@composable
            Log.d("MetronomeEngine", "NavGraph: Entering LIVE_AUTOPLAY: setId=$setId")
            val vm: LiveViewModel = viewModel(factory = viewModelFactory)
            Log.d("MetronomeEngine", "NavGraph: LiveViewModel created")
            LivePerformanceScreen(
                setId = setId,
                viewModel = vm,
                onExit = { navController.popBackStack() },
                autoplay = true
            )
        }
        composable(
            Routes.LIVE,
            arguments = listOf(navArgument("setId") { type = NavType.LongType })
        ) { entry ->
            val setId = entry.arguments?.getLong("setId") ?: return@composable
            Log.d("MetronomeEngine", "NavGraph: Entering LIVE: setId=$setId")
            val vm: LiveViewModel = viewModel(factory = viewModelFactory)
            Log.d("MetronomeEngine", "NavGraph: LiveViewModel created")
            LivePerformanceScreen(
                setId = setId,
                viewModel = vm,
                onExit = { navController.popBackStack() },
                autoplay = false
            )
        }
        composable(Routes.METRONOME) {
            Log.d("MetronomeEngine", "NavGraph: Entering METRONOME")
            val vm: StandaloneMetronomeViewModel = viewModel(factory = viewModelFactory)
            Log.d("MetronomeEngine", "NavGraph: StandaloneMetronomeViewModel created")
            MetronomeScreen(viewModel = vm, modifier = modifier)
        }
        composable(Routes.ABOUT) {
            AboutScreen()
        }
        composable(Routes.SETTINGS) {
            val vm = SettingsViewModel(preferencesManager = preferencesManager)
            SettingsScreen(viewModel = vm)
        }
    }
}

@Composable
private fun PlaceholderScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message)
    }
}