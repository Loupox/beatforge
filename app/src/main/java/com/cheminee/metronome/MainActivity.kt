package com.cheminee.metronome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.cheminee.metronome.repository.SetRepository
import com.cheminee.metronome.ui.AppNavGraph
import com.cheminee.metronome.ui.AppTopBarMenu
import com.cheminee.metronome.ui.AppViewModelFactory
import com.cheminee.metronome.ui.Routes
import com.cheminee.metronome.ui.theme.ChemineeTheme

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

        setContent {
            ChemineeTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                var triggerImport by remember { mutableStateOf(false) }

                val routesWithMenu = listOf(Routes.SETS, Routes.EDITOR, Routes.METRONOME, Routes.LIVE, Routes.ABOUT, Routes.SETTINGS)

                Scaffold(
                    topBar = {
                        if (currentRoute in routesWithMenu) {
                            TopAppBar(
                                title = { Text(getString(R.string.app_name)) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                actions = {
                                    AppTopBarMenu(
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
                                        onNavigateToAbout = {
                                            if (currentRoute != Routes.ABOUT) {
                                                navController.navigate(Routes.ABOUT)
                                            }
                                        },
                                        onNavigateToSettings = {
                                            if (currentRoute != Routes.SETTINGS) {
                                                navController.navigate(Routes.SETTINGS)
                                            }
                                        },
                                        onImportClick = {
                                            if (currentRoute != Routes.SETS) {
                                                navController.navigate(Routes.SETS) {
                                                    popUpTo(Routes.SETS) { inclusive = true }
                                                }
                                            }
                                            triggerImport = true
                                        }
                                    )
                                }
                            )
                        }
                    }
                ) { padding ->
                    AppNavGraph(
                        navController = navController,
                        viewModelFactory = factory,
                        preferencesManager = preferencesManager,
                        modifier = Modifier.padding(padding),
                        triggerImport = triggerImport,
                        onImportHandled = { triggerImport = false }
                    )
                }
            }
        }
    }
}