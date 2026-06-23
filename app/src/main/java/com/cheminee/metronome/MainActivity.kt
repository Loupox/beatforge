package com.cheminee.metronome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cheminee.metronome.data.AppDatabase
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.metronome.MetronomeEngine
import com.cheminee.metronome.repository.SetRepository
import com.cheminee.metronome.ui.AppBottomNavBar
import com.cheminee.metronome.ui.AppNavGraph
import com.cheminee.metronome.ui.AppViewModelFactory
import com.cheminee.metronome.ui.PlusBottomSheet
import com.cheminee.metronome.ui.Routes
import com.cheminee.metronome.ui.theme.BeatForgeTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    private val factory: ViewModelProvider.Factory by lazy {
        val db = AppDatabase.get(applicationContext)
        AppViewModelFactory(SetRepository(db), preferencesManager, applicationContext)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(applicationContext)
        MetronomeEngine.setPreferences(preferencesManager)
        MetronomeEngine.setContext(applicationContext)

        setContent {
                val darkThemeEnabled by preferencesManager.darkThemeEnabled.collectAsState()
                BeatForgeTheme(useDarkTheme = darkThemeEnabled) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    var triggerImport by remember { mutableStateOf(false) }
                    var showPlus by remember { mutableStateOf(false) }
                    val sheetState = rememberModalBottomSheetState()
                    val soundEnabled by preferencesManager.soundEnabled.collectAsState()
                    val vibrationEnabled by preferencesManager.vibrationEnabled.collectAsState()

                    val bottomNavRoutes = listOf(Routes.SETS, Routes.METRONOME)
                    val showBottomNav = currentRoute in bottomNavRoutes
                            || currentRoute == Routes.SETTINGS
                            || currentRoute == Routes.ABOUT
                            || currentRoute?.startsWith("editor/") == true

                    Scaffold(
                        bottomBar = {
                            if (showBottomNav) {
                                AppBottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigateToSets = {
                                        if (currentRoute != Routes.SETS) {
                                            navController.navigate(Routes.SETS) {
                                                popUpTo(Routes.SETS) { inclusive = true }
                                            }
                                        }
                                    },
                                    onNavigateToMetronome = {
                                        if (currentRoute != Routes.METRONOME) {
                                            navController.navigate(Routes.METRONOME)
                                        }
                                    },
                                    onPlusClick = { showPlus = true }
                                )
                            }
                        }
                    ) { padding ->
                        val scaffoldPadding = padding
                    AppNavGraph(
                        navController = navController,
                        viewModelFactory = factory,
                        preferencesManager = preferencesManager,
                        modifier = Modifier.padding(scaffoldPadding),
                        triggerImport = triggerImport,
                        onImportHandled = { triggerImport = false }
                    )
                }

                if (showPlus) {
                    PlusBottomSheet(
                        sheetState = sheetState,
                        soundEnabled = soundEnabled,
                        vibrationEnabled = vibrationEnabled,
                        onToggleSound = { preferencesManager.setSoundEnabled(!soundEnabled) },
                        onToggleVibration = { preferencesManager.setVibrationEnabled(!vibrationEnabled) },
                        onNavigateToSettings = {
                            if (currentRoute != Routes.SETTINGS) {
                                navController.navigate(Routes.SETTINGS)
                            }
                        },
                        onNavigateToAbout = {
                            if (currentRoute != Routes.ABOUT) {
                                navController.navigate(Routes.ABOUT)
                            }
                        },
                        onImportClick = {
                            if (currentRoute != Routes.SETS) {
                                navController.navigate(Routes.SETS) {
                                    popUpTo(Routes.SETS) { inclusive = true }
                                }
                            }
                            triggerImport = true
                        },
                        onDismiss = { showPlus = false }
                    )
                }
            }
        }
    }
}