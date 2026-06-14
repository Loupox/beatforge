package com.cheminee.metronome.ui.metronome

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheminee.metronome.R
import com.cheminee.metronome.data.PreferencesManager
import com.cheminee.metronome.ui.theme.Spacing

private const val DEFAULT_BEATS_PER_BAR = 4

@Composable
fun VelocitySlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 30f..300f,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value.toFloat(),
        onValueChange = { newValue ->
            onValueChange(newValue.toInt())
        },
        valueRange = valueRange,
        modifier = modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun MetronomeScreen(viewModel: StandaloneMetronomeViewModel) {
    val engine = viewModel.engine
    val running by engine.running.collectAsState()
    val beatIndex by engine.beatIndex.collectAsState()
    val flashing by engine.flash.collectAsState()
    val flashEnabled by (viewModel.flashEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val flashColorIndex by (viewModel.flashColorIndex?.collectAsState(initial = 0) ?: remember { mutableStateOf(0) })
    val soundEnabled by (viewModel.soundEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val vibrationEnabled by (viewModel.vibrationEnabled?.collectAsState(initial = false) ?: remember { mutableStateOf(false) })

    val flashColors = PreferencesManager.FLASH_COLORS
    val flashColor = Color(flashColors.getOrElse(flashColorIndex) { flashColors[0] })
    val bgColor = if (flashing && running && flashEnabled) flashColor else MaterialTheme.colorScheme.background
    val animatedBgColor by animateColorAsState(targetValue = bgColor, animationSpec = tween(durationMillis = 300))

    var showBpmDialog by remember { mutableStateOf(false) }
    var bpmInput by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(color = animatedBgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Box(
            modifier = Modifier.clickable { showBpmDialog = true }
        ) {
            Text(
                text = "${viewModel.bpm}",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            flashColors.forEachIndexed { index, colorInt ->
                val color = Color(colorInt)
                val isSelected = index == flashColorIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { viewModel.setFlashColorIndex(index) }
                )
            }
        }

        Text(
            text = "BPM",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.onTap() },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = stringResource(R.string.tap_tempo),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md)
            ) {
                Text(
                    text = "Tempo",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
                VelocitySlider(
                    value = viewModel.bpm,
                    onValueChange = { newValue -> viewModel.setBpm(newValue) },
                    valueRange = 30f..300f
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("30", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("300", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        if (running) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(DEFAULT_BEATS_PER_BAR) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == beatIndex) 24.dp else 14.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == beatIndex)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { viewModel.toggle() },
                modifier = Modifier.size(96.dp)
            ) {
                Icon(
                    imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (running) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.toggleSound() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (soundEnabled) "Son activé" else "Son désactivé",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = { viewModel.toggleVibration() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = if (vibrationEnabled) "Vibration activée" else "Vibration désactivée",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
    }

    if (showBpmDialog) {
        AlertDialog(
            onDismissRequest = { showBpmDialog = false },
            title = { Text(stringResource(R.string.enter_bpm_title)) },
            text = {
                OutlinedTextField(
                    value = bpmInput,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            bpmInput = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.enter_bpm_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newBpm = bpmInput.toIntOrNull()?.coerceIn(30, 300)
                        if (newBpm != null) {
                            viewModel.setBpm(newBpm)
                        }
                        showBpmDialog = false
                        bpmInput = ""
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBpmDialog = false
                    bpmInput = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}