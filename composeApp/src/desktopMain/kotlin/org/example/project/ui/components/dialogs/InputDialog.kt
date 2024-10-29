package org.example.project.ui.components.dialogs

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add
import budgetmanager.composeapp.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource


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
        confirmButtonText = stringResource(Res.string.add),
        dismissButtonText = stringResource(Res.string.cancel),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            shape = MaterialTheme.shapes.medium,
            label = { Text(label) },
            maxLines = 1,
            isError = isError,
            modifier = Modifier.widthIn(min = 300.dp),
        )
    }
}