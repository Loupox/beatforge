package com.cheminee.metronome.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cheminee.metronome.data.TimeSignature

@Composable
fun TimeSignaturePicker(
    selectedTimeSignature: TimeSignature,
    onTimeSignatureSelected: (TimeSignature) -> Unit,
    customNumerator: Int,
    customBeatUnit: Int,
    onCustomSave: (numerator: Int, beatUnit: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeSignature.PRESETS.forEach { ts ->
            TimeSignatureChip(
                timeSignature = ts,
                isSelected = ts == selectedTimeSignature,
                onClick = { onTimeSignatureSelected(ts) }
            )
        }
        CustomTimeSignatureChip(
            isSelected = selectedTimeSignature is TimeSignature.Custom,
            displayName = if (selectedTimeSignature is TimeSignature.Custom)
                selectedTimeSignature.displayName
            else
                "$customNumerator/$customBeatUnit",
            onClick = { showDialog = true }
        )
    }

    if (showDialog) {
        CustomTimeSignatureDialog(
            initialNumerator = customNumerator,
            initialBeatUnit = customBeatUnit,
            onConfirm = { num, unit ->
                showDialog = false
                onCustomSave(num, unit)
            },
            onDismiss = {
                if (showDialog) showDialog = false
            }
        )
    }
}

@Composable
private fun TimeSignatureChip(
    timeSignature: TimeSignature,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeSignature.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
private fun CustomTimeSignatureChip(
    isSelected: Boolean,
    displayName: String,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(if (isSelected) 56.dp else 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isSelected) displayName else "+",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
fun TimeSignatureDisplay(
    displayName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}