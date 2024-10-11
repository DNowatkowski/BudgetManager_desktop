package org.example.project.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initialText) }
    BudgetManagerDialog(
        onDismiss = onDismiss,
        onConfirmed = { onConfirmed(text) },
        title = title,
        confirmEnabled = text.isNotBlank(),
        confirmButtonText = "Add",
        dismissButtonText = "Cancel",
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
    }

}