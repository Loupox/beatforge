package com.cheminee.metronome.ui.live

import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheminee.metronome.R
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.ui.components.BeatDots
import com.cheminee.metronome.ui.components.ChemineeTopBar
import com.cheminee.metronome.ui.components.FlashColorPicker
import com.cheminee.metronome.ui.theme.BeatForgeTextStyles
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
    LaunchedEffect(setId) {
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
    val accentFirstBeatEnabled by (viewModel.accentFirstBeatEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val isLoading by viewModel.isLoading.collectAsState()

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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.loading),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    if (songs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.no_songs_in_set),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { songs.size })
    val coroutineScope = rememberCoroutineScope()
    val currentSong = songs.getOrNull(pagerState.currentPage)

    LaunchedEffect(pagerState, songs) {
        var prevPage = -1
        snapshotFlow { pagerState.currentPage to pagerState.currentPageOffsetFraction }.collect { (current, offset) ->
            if (prevPage == -1) {
                prevPage = current
                return@collect
            }
            if (prevPage == 0 && offset < -0.5f) {
                pagerState.animateScrollToPage(songs.lastIndex)
                prevPage = songs.lastIndex
            } else if (prevPage == songs.lastIndex && offset > 0.5f) {
                pagerState.animateScrollToPage(0)
                prevPage = 0
            } else if (current != prevPage) {
                prevPage = current
            }
        }
    }

    val flashColors = com.cheminee.metronome.data.PreferencesManager.FLASH_COLORS
    val isFirstBeatAccented = accentFirstBeatEnabled && beatIndex == 0
    val flashColor = if (isFirstBeatAccented) {
        Color(flashColors.getOrElse(6) { flashColors[6] })
    } else {
        Color(flashColors.getOrElse(flashColorIndex) { flashColors[0] })
    }
    val bgColor = if (flashing && flashEnabled) flashColor else MaterialTheme.colorScheme.background
    val animatedBgColor by animateColorAsState(targetValue = bgColor, animationSpec = tween(durationMillis = 300))
    val barsAlpha by animateFloatAsState(targetValue = if (flashing && flashEnabled) 0.6f else 1f, animationSpec = tween(durationMillis = 300), label = "barsAlpha")

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
        Column(modifier = Modifier.fillMaxSize().alpha(barsAlpha)) {
            ChemineeTopBar(
                title = currentSong?.name ?: stringResource(R.string.nav_live),
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stop()
                        onExit()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.quitter),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSound() }) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = if (soundEnabled) stringResource(R.string.sound_on) else stringResource(R.string.sound_off),
                            tint = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.toggleVibration() }) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = if (vibrationEnabled) stringResource(R.string.vibration_on) else stringResource(R.string.vibration_off),
                            tint = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(Spacing.lg))

                Text(
                    text = currentSong?.bpm?.toString() ?: "--",
                    style = BeatForgeTextStyles.bpmLive,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.bpm_label),
                    style = BeatForgeTextStyles.microLabel,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.size(Spacing.md))

                FlashColorPicker(
                    selectedIndex = flashColorIndex,
                    onColorSelected = { viewModel.setFlashColorIndex(it) }
                )

                Spacer(modifier = Modifier.size(Spacing.lg))

                BeatDots(
                    beatIndex = beatIndex,
                    running = running,
                    beatsPerBar = viewModel.engine.currentBeatsPerBar,
                    showSubdots = false
                )
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
                    SongPage(song = song)
                }
            }

            val nextSongIndex = (pagerState.currentPage + 1) % songs.size
            val nextSong = songs.getOrNull(nextSongIndex)

            if (nextSong != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.next_song_label),
                                style = BeatForgeTextStyles.microLabel,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = nextSong.name,
                                style = BeatForgeTextStyles.cardLabel,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${nextSong.bpm}",
                            style = BeatForgeTextStyles.screenTitle,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            ControlBar(
                running = running,
                modifier = Modifier.alpha(barsAlpha),
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
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.xl, top = Spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.previous_song),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onToggle, modifier = Modifier.size(96.dp)) {
                Icon(
                    imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (running) stringResource(R.string.pause) else stringResource(R.string.play),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.next_song),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun SongPage(song: Song) {
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
                if (song.comments.isNotBlank()) {
                    Spacer(modifier = Modifier.size(Spacing.md))
                    Text(
                        text = song.comments,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun snapshotFlowOfPage(pagerState: androidx.compose.foundation.pager.PagerState) =
    androidx.compose.runtime.snapshotFlow { pagerState.currentPage }