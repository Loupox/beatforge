package com.cheminee.metronome.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cheminee.metronome.ui.theme.BeatForgeColors

@Composable
fun BeatDots(
    beatIndex: Int,
    running: Boolean,
    beatsPerBar: Int = 4,
    showSubdots: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(beatsPerBar) { i ->
                val isActive = i == beatIndex
                val isBeatOne = i == 0
                val size by animateDpAsState(
                    targetValue = when {
                        !running -> if (isBeatOne) 28.dp else 18.dp
                        isBeatOne -> 28.dp
                        isActive -> 18.dp
                        else -> if (isBeatOne) 28.dp else 18.dp
                    },
                    animationSpec = tween(durationMillis = 150),
                    label = "dotSize"
                )
                val isDark = MaterialTheme.colorScheme.primary == BeatForgeColors.BronzeCharbon.accent
                val fillColor by animateColorAsState(
                    targetValue = when {
                        isBeatOne && isActive -> if (isDark) BeatForgeColors.BronzeCharbon.accentLight else BeatForgeColors.BoisClair.accentLight
                        isActive && running -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surface
                    },
                    animationSpec = tween(durationMillis = 150),
                    label = "dotColor"
                )
                val borderColor by animateColorAsState(
                    targetValue = when {
                        isBeatOne -> if (isDark) BeatForgeColors.BronzeCharbon.textMuted else BeatForgeColors.BoisClair.border
                        !isActive || !running -> MaterialTheme.colorScheme.outline
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                    },
                    animationSpec = tween(durationMillis = 150),
                    label = "borderColor"
                )
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(fillColor)
                        .then(
                            if (isBeatOne || !isActive || !running) {
                                Modifier.border(1.dp, borderColor, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }

        if (showSubdots) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(8) { i ->
                    val subActive = running && isSubdotActive(beatIndex, i, beatsPerBar)
                    val subColor by animateColorAsState(
                        targetValue = if (subActive) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        },
                        animationSpec = tween(durationMillis = 100),
                        label = "subdotColor"
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(subColor)
                    )
                }
            }
        }
    }
}

private fun isSubdotActive(beatIndex: Int, subdotIndex: Int, beatsPerBar: Int): Boolean {
    if (subdotIndex == 0) return false
    val subBeatsPerBeat = 8 / beatsPerBar.coerceAtLeast(1)
    val currentBeatSubdots = beatIndex * subBeatsPerBeat
    return subdotIndex == currentBeatSubdots + subBeatsPerBeat / 2
}