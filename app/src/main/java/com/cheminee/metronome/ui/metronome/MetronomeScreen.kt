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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheminee.metronome.R
import com.cheminee.metronome.data.TimeSignature
import com.cheminee.metronome.ui.components.BeatDots
import com.cheminee.metronome.ui.components.ChemineeTopBar
import com.cheminee.metronome.ui.components.FlashColorButton
import com.cheminee.metronome.ui.components.TimeSignatureDisplay
import com.cheminee.metronome.ui.components.TimeSignaturePicker
import com.cheminee.metronome.ui.theme.BeatForgeTextStyles
import com.cheminee.metronome.ui.theme.BorderThickness
import com.cheminee.metronome.ui.theme.Spacing

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetronomeScreen(
    viewModel: StandaloneMetronomeViewModel,
    modifier: Modifier = Modifier
) {
    val engine = viewModel.engine
    val running by engine.running.collectAsState()
    val beatIndex by engine.beatIndex.collectAsState()
    val flashing by engine.flash.collectAsState()
    val flashEnabled by (viewModel.flashEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val flashColorIndex by (viewModel.flashColorIndex?.collectAsState(initial = 0) ?: remember { mutableStateOf(0) })
    val soundEnabled by (viewModel.soundEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val vibrationEnabled by (viewModel.vibrationEnabled?.collectAsState(initial = false) ?: remember { mutableStateOf(false) })
    val accentFirstBeatEnabled by (viewModel.accentFirstBeatEnabled?.collectAsState(initial = true) ?: remember { mutableStateOf(true) })
    val timeSignature by (viewModel.timeSignature?.collectAsState(initial = TimeSignature.FOUR_FOUR) ?: remember { mutableStateOf(TimeSignature.FOUR_FOUR) })

    val customNumerator = if (timeSignature is TimeSignature.Custom) (timeSignature as TimeSignature.Custom).numerator else 4
    val customBeatUnit = if (timeSignature is TimeSignature.Custom) (timeSignature as TimeSignature.Custom).beatUnit else 4

    val flashColors = com.cheminee.metronome.data.PreferencesManager.FLASH_COLORS
    val isFirstBeatAccented = accentFirstBeatEnabled && beatIndex == 0
    val flashColor = if (isFirstBeatAccented) {
        Color(flashColors.getOrElse(6) { flashColors[6] })
    } else {
        Color(flashColors.getOrElse(flashColorIndex) { flashColors[0] })
    }
    val bgColor = if (flashing && running && flashEnabled) flashColor else MaterialTheme.colorScheme.background
    val animatedBgColor by animateColorAsState(targetValue = bgColor, animationSpec = tween(durationMillis = 300))

    var showBpmDialog by remember { mutableStateOf(false) }
    var bpmInput by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize().background(color = animatedBgColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChemineeTopBar(
                title = "BeatForge",
                actions = {
                    IconButton(onClick = { viewModel.toggleSound() }) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = if (soundEnabled) "Son activé" else "Son désactivé",
                            tint = if (soundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.toggleVibration() }) {
                        Icon(
                            imageVector = if (vibrationEnabled) Icons.Default.Vibration else Icons.Default.DoNotDisturb,
                            contentDescription = if (vibrationEnabled) "Vibration activée" else "Vibration désactivée",
                            tint = if (vibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.clickable { showBpmDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(BorderStroke(BorderThickness.active, MaterialTheme.colorScheme.outline), CircleShape)
                                .clickable { viewModel.decrementBpm() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "-1 BPM",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "${viewModel.bpm}",
                            style = BeatForgeTextStyles.bpm,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = Spacing.lg)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(BorderStroke(BorderThickness.active, MaterialTheme.colorScheme.outline), CircleShape)
                                .clickable { viewModel.incrementBpm() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "+1 BPM",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "B P M",
                        style = BeatForgeTextStyles.microLabel,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = androidx.compose.ui.unit.TextUnit(3.5f, androidx.compose.ui.unit.TextUnitType.Sp)
                    )
                }

                FlashColorButton(
                    selectedIndex = flashColorIndex,
                    onColorSelected = { viewModel.setFlashColorIndex(it) }
                )

                BeatDots(
                    beatIndex = beatIndex,
                    running = running,
                    beatsPerBar = engine.currentBeatsPerBar,
                    showSubdots = true
                )

                TimeSignatureDisplay(displayName = engine.currentTimeSignatureDisplay)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.time_signature),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        TimeSignaturePicker(
                            selectedTimeSignature = timeSignature,
                            onTimeSignatureSelected = { viewModel.setTimeSignature(it) },
                            customNumerator = customNumerator,
                            customBeatUnit = customBeatUnit,
                            onCustomSave = { num, unit -> viewModel.setCustomTimeSignature(num, unit) }
                        )
                    }
                }

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

                Spacer(modifier = Modifier.size(Spacing.md))

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
                        Slider(
                            value = viewModel.bpm.toFloat(),
                            onValueChange = { viewModel.setBpm(it.toInt()) },
                            valueRange = 45f..250f,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "45",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "250",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.toggle() },
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (running) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
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
                        val newBpm = bpmInput.toIntOrNull()?.coerceIn(45, 250)
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