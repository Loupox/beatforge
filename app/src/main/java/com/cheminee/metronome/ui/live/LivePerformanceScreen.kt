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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheminee.metronome.R
import com.cheminee.metronome.data.Song
import com.cheminee.metronome.ui.components.BeatDots
import com.cheminee.metronome.ui.components.ChemineeTopBar
import com.cheminee.metronome.ui.theme.BeatForgeColors
import com.cheminee.metronome.ui.theme.BeatForgeTextStyles
import com.cheminee.metronome.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn()
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

    var currentIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val currentSong = songs.getOrNull(currentIndex)

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

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize().background(animatedBgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(barsAlpha)
        ) {
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

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                        Spacer(modifier = Modifier.size(Spacing.lg))
                        BeatDots(
                            beatIndex = beatIndex,
                            running = running,
                            beatsPerBar = viewModel.engine.currentBeatsPerBar,
                            showSubdots = false
                        )
                        Spacer(modifier = Modifier.size(Spacing.lg))
                        Text(
                            text = currentSong?.name ?: "",
                            style = BeatForgeTextStyles.screenTitle,
                            color = BeatForgeColors.BronzeCharbon.textPrimary
                        )
                        Text(
                            text = currentSong?.comments?.let { "$it · " }?.plus(viewModel.engine.currentTimeSignatureDisplay)
                                ?: viewModel.engine.currentTimeSignatureDisplay,
                            style = BeatForgeTextStyles.microLabel,
                            color = BeatForgeColors.BronzeCharbon.textMuted
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ControlBar(
                            running = running,
                            modifier = Modifier.fillMaxWidth(),
                            onPrevious = {
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else songs.lastIndex
                                songs.getOrNull(currentIndex)?.let {
                                    if (running) viewModel.playFor(it.bpm)
                                }
                            },
                            onToggle = {
                                currentSong?.let { viewModel.toggle(it.bpm) }
                            },
                            onNext = {
                                currentIndex = (currentIndex + 1) % songs.size
                                songs.getOrNull(currentIndex)?.let {
                                    if (running) viewModel.playFor(it.bpm)
                                }
                            }
                        )
                    }
                }
                SongsMiniList(
                    songs = songs,
                    currentIndex = currentIndex,
                    onSongClick = { index ->
                        currentIndex = index
                        songs.getOrNull(index)?.let {
                            if (running) viewModel.playFor(it.bpm)
                        }
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currentSong?.let { viewModel.toggle(it.bpm) } },
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
                    Spacer(modifier = Modifier.size(Spacing.lg))
                    BeatDots(
                        beatIndex = beatIndex,
                        running = running,
                        beatsPerBar = viewModel.engine.currentBeatsPerBar,
                        showSubdots = false
                    )
                    Spacer(modifier = Modifier.size(Spacing.lg))
                    Text(
                        text = currentSong?.name ?: "",
                        style = BeatForgeTextStyles.screenTitle,
                        color = BeatForgeColors.BronzeCharbon.textPrimary
                    )
                    if (!currentSong?.comments.isNullOrBlank()) {
                        Spacer(modifier = Modifier.size(Spacing.xs))
                        Text(
                            text = "${currentSong?.comments} · ${viewModel.engine.currentTimeSignatureDisplay}",
                            style = BeatForgeTextStyles.microLabel,
                            color = BeatForgeColors.BronzeCharbon.textMuted
                        )
                    } else {
                        Spacer(modifier = Modifier.size(Spacing.xs))
                        Text(
                            text = viewModel.engine.currentTimeSignatureDisplay,
                            style = BeatForgeTextStyles.microLabel,
                            color = BeatForgeColors.BronzeCharbon.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                SongsMiniList(
                    songs = songs,
                    currentIndex = currentIndex,
                    onSongClick = { index ->
                        currentIndex = index
                        songs.getOrNull(index)?.let {
                            if (running) viewModel.playFor(it.bpm)
                        }
                    }
                )

                ControlBar(
                    running = running,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(barsAlpha),
                    onPrevious = {
                        currentIndex = if (currentIndex > 0) currentIndex - 1 else songs.lastIndex
                        songs.getOrNull(currentIndex)?.let {
                            if (running) viewModel.playFor(it.bpm)
                        }
                    },
                    onToggle = {
                        currentSong?.let { viewModel.toggle(it.bpm) }
                    },
                    onNext = {
                        currentIndex = (currentIndex + 1) % songs.size
                        songs.getOrNull(currentIndex)?.let {
                            if (running) viewModel.playFor(it.bpm)
                        }
                    }
                )
            }
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
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            .navigationBarsPadding(),
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
private fun SongsMiniList(
    songs: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        if (songs.isNotEmpty()) {
            listState.animateScrollToItem(
                index = currentIndex,
                scrollOffset = -(listState.layoutInfo.viewportSize.height / 3)
            )
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .height(200.dp)
    ) {
        itemsIndexed(songs) { index, song ->
            val isActive = index == currentIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isActive) BeatForgeColors.BronzeCharbon.surface2 else BeatForgeColors.BronzeCharbon.surface)
                    .clickable { onSongClick(index) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) BeatForgeColors.BronzeCharbon.accent
                            else BeatForgeColors.BronzeCharbon.border
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = BeatForgeTextStyles.microLabel,
                        color = BeatForgeColors.BronzeCharbon.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.size(Spacing.sm))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.name,
                        style = BeatForgeTextStyles.cardLabel,
                        color = if (isActive) {
                            BeatForgeColors.BronzeCharbon.textPrimary
                        } else {
                            BeatForgeColors.BronzeCharbon.textMuted
                        }
                    )
                    if (song.comments.isNotBlank()) {
                        Text(
                            text = song.comments,
                            style = BeatForgeTextStyles.microLabel,
                            color = BeatForgeColors.BronzeCharbon.textMuted
                        )
                    }
                }
                Text(
                    text = "${song.bpm}",
                    style = BeatForgeTextStyles.screenTitle,
                    color = if (isActive) {
                        BeatForgeColors.BronzeCharbon.accentLight
                    } else {
                        BeatForgeColors.BronzeCharbon.textMuted
                    }
                )
                Spacer(modifier = Modifier.size(Spacing.sm))
            }
        }
    }
}