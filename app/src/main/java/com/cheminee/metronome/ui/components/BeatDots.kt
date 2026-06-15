package com.cheminee.metronome.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BeatDots(
    beatIndex: Int,
    running: Boolean,
    beatsPerBar: Int = 4,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(beatsPerBar) { i ->
            val isActive = i == beatIndex
            val size by animateDpAsState(
                targetValue = if (isActive && running) 24.dp else 14.dp,
                animationSpec = tween(durationMillis = 150),
                label = "dotSize"
            )
            val color by animateColorAsState(
                targetValue = if (isActive && running)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                animationSpec = tween(durationMillis = 150),
                label = "dotColor"
            )
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}