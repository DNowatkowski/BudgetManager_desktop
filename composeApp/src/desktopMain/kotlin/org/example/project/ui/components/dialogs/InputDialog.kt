package org.example.project.ui.components.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun InputDialog(
    title: String,
    initialText: String = "",
    label: String,
    numbersOnly: Boolean = false,
    onConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initialText) }
    var isError by remember { mutableStateOf(false) }

    if (numbersOnly)
        LaunchedEffect(text) {
            isError = text.toDoubleOrNull() == null
        }

    BudgetManagerDialog(
        onDismiss = onDismiss,
        onConfirmed = { onConfirmed(text) },
        title = title,
        confirmEnabled = text.isNotBlank() && !isError,
        confirmButtonText = "Add",
        dismissButtonText = "Cancel",
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) },
            maxLines = 1,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
        )
    }

}