package com.cheminee.metronome.ui.live

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import android.util.Log

import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.ui.theme.Spacing
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LivePerformanceScreen(
    setId: Long,
    viewModel: LiveViewModel,
    onExit: () -> Unit,
    autoplay: Boolean = false
) {
    Log.d("MetronomeEngine", "LiveScreen composing START: setId=$setId, autoplay=$autoplay")
    LaunchedEffect(setId) {
        Log.d("MetronomeEngine", "LiveScreen LaunchedEffect triggered, calling bind($setId, $autoplay)")
        viewModel.bind(setId, autoplay)
    }
    val songs by viewModel.songs.collectAsState()
    val flashing by viewModel.engine.flash.collectAsState()
    val running by viewModel.engine.running.collectAsState()
    val beatIndex by viewModel.engine.beatIndex.collectAsState()
    val flashEnabled by (viewModel.flashEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val flashColorIndex by (viewModel.flashColorIndex?.collectAsState(initial = 0) ?: remember { mutableStateOf(0) })
    val soundEnabled by (viewModel.soundEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val vibrationEnabled by (viewModel.vibrationEnabled?.collectAsState(initial = false) ?: remember { mutableStateOf(false) })
    val isLoading by viewModel.isLoading.collectAsState()
    Log.d("MetronomeEngine", "LiveScreen state: isLoading=$isLoading, songs.size=${songs.size}, running=$running")

    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window?.decorView?.let { decorView ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }
        onDispose {
            window?.decorView?.let { decorView ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.show(WindowInsets.Type.systemBars())
                } else {
                    @Suppress("DEPRECATION")
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            viewModel.stop()
        }
    }

    BackHandler {
        viewModel.stop()
        onExit()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Chargement...",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Pas de morceau dans ce set.",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { songs.size })
    val coroutineScope = rememberCoroutineScope()
    val currentSong = songs.getOrNull(pagerState.currentPage)

    val pageFlow = snapshotFlow { pagerState.currentPage }
    LaunchedEffect(pagerState, songs) {
        var prevPage = -1
        pageFlow.collect { current ->
            if (prevPage == -1) {
                prevPage = current
                return@collect
            }
            if (prevPage == 0 && current == songs.lastIndex) {
                Log.d("LiveWrap", "wrap→ LEFT: $prevPage → $current, scroll to ${songs.lastIndex}")
                pagerState.scrollToPage(songs.lastIndex)
            } else if (prevPage == songs.lastIndex && current == 0) {
                Log.d("LiveWrap", "wrap→ RIGHT: $prevPage → $current, scroll to 0")
                pagerState.scrollToPage(0)
            }
            prevPage = current
        }
    }
    val flashColors = PreferencesManager.FLASH_COLORS
    val flashColor = androidx.compose.ui.graphics.Color(flashColors.getOrElse(flashColorIndex) { flashColors[0] })
    val bgColor = if (flashing && flashEnabled) flashColor else MaterialTheme.colorScheme.background
    val animatedBgColor by animateColorAsState(targetValue = bgColor, animationSpec = tween(durationMillis = 300))

    LaunchedEffect(pagerState, songs, running) {
        snapshotFlowOfPage(pagerState)
            .distinctUntilChanged()
            .collect { page ->
                val song = songs.getOrNull(page) ?: return@collect
                if (running) viewModel.playFor(song.bpm) else viewModel.stop()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { currentSong?.let { viewModel.toggle(it.bpm) } }
            .background(animatedBgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        text = currentSong?.name ?: "Live",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stop()
                        onExit()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quitter",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.toggleSound()
                    }) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (soundEnabled) "Son activé" else "Son désactivé",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        viewModel.toggleVibration()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = if (vibrationEnabled) "Vibration activée" else "Vibration désactivée",
                            tint = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xCC1C1C1C),
                    titleContentColor = Color(0xFFF7F4F0)
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.sm),
                horizontalArrangement = Arrangement.Center
            ) {
                flashColors.forEachIndexed { index, colorInt ->
                    val color = Color(colorInt)
                    val isSelected = index == flashColorIndex
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { viewModel.setFlashColorIndex(index) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondBoundsPageCount = 1
                ) { page ->
                    val song = songs[page]
                    SongPage(song = song, beatIndex = beatIndex, running = running)
                }
            }

            ControlBar(
                running = running,
                onPrevious = {
                    coroutineScope.launch {
                        val prevPage = if (pagerState.currentPage > 0) {
                            pagerState.currentPage - 1
                        } else {
                            songs.lastIndex
                        }
                        pagerState.animateScrollToPage(prevPage)
                    }
                },
                onToggle = {
                    currentSong?.let { viewModel.toggle(it.bpm) }
                },
                onNext = {
                    coroutineScope.launch {
                        val nextPage = (pagerState.currentPage + 1) % songs.size
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
            )
        }
    }
}

@Composable
private fun ControlBar(
    running: Boolean,
    onPrevious: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.xl, top = Spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Morceau précédent",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (running) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Morceau suivant",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun SongPage(song: Song, beatIndex: Int, running: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.size(Spacing.sm))
                Text(
                    text = "${song.bpm} BPM",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (song.comments.isNotBlank()) {
                    Spacer(modifier = Modifier.size(Spacing.md))
                    Text(
                        text = song.comments,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xs)
                    )
                }
                if (running) {
                    Spacer(modifier = Modifier.size(Spacing.lg))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        repeat(4) { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == beatIndex) 24.dp else 14.dp)
                                    .background(
                                        color = if (i == beatIndex)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun snapshotFlowOfPage(pagerState: androidx.compose.foundation.pager.PagerState) =
    androidx.compose.runtime.snapshotFlow { pagerState.currentPage }