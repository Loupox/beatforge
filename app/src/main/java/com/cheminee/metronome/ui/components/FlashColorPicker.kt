package com.cheminee.metronome.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cheminee.metronome.data.PreferencesManager

@Composable
fun FlashColorPicker(
    selectedIndex: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Int> = PreferencesManager.FLASH_COLORS
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        colors.forEachIndexed { index, colorInt ->
            val color = Color(colorInt)
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isSelected) 28.dp else 24.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Gray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(index) }
            )
        }
    }
}