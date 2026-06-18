package com.cheminee.metronome.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CustomTimeSignatureDialog(
    initialNumerator: Int,
    initialBeatUnit: Int,
    onConfirm: (numerator: Int, beatUnit: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val numFocusRequester = remember { FocusRequester() }
    val unitFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var numText by remember(initialNumerator) {
        mutableStateOf(
            TextFieldValue(
                initialNumerator.toString().takeLast(1),
                TextRange(0, 1)
            )
        )
    }
    var unitText by remember(initialBeatUnit) {
        mutableStateOf(
            TextFieldValue(
                initialBeatUnit.toString().takeLast(1),
                TextRange(0, 1)
            )
        )
    }

    LaunchedEffect(Unit) {
        numFocusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Signature personnalis\u00e9e",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Entrez le num\u00e9rateur et le d\u00e9nominateur",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumericField(
                        value = numText,
                        onValueChange = { newValue ->
                            if (newValue.text.length <= 1 && newValue.text.all { it.isDigit() }) {
                                numText = newValue
                                if (newValue.text.isNotEmpty()) {
                                    unitFocusRequester.requestFocus()
                                }
                            }
                        },
                        placeholder = "4",
                        focusRequester = numFocusRequester,
                        onNext = { unitFocusRequester.requestFocus() },
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        "/",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    NumericField(
                        value = unitText,
                        onValueChange = { newValue ->
                            if (newValue.text.length <= 1 && newValue.text.all { it.isDigit() }) {
                                unitText = newValue
                            }
                        },
                        placeholder = "4",
                        focusRequester = unitFocusRequester,
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        modifier = Modifier.width(72.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val num = numText.text.toIntOrNull() ?: 4
                    val unit = unitText.text.toIntOrNull() ?: 4
                    onConfirm(num, unit)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun NumericField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester,
    onNext: () -> Unit = {},
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = if (onNext != {}) ImeAction.Next else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext() },
                onDone = { onDone() }
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (value.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            },
            modifier = Modifier.focusRequester(focusRequester)
        )
    }
}