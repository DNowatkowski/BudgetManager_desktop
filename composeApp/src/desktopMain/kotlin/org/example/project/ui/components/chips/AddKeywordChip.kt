package org.example.project.ui.components.chips

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.dialogs.InputDialog

@Composable
fun AddKeywordChip(
    onAddKeyword: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        InputDialog(
            title = "Add Keyword",
            onConfirmed = onAddKeyword,
            onDismiss = { showDialog = false },
            label = "Keyword",
        )
    }
    InputChip(
        selected = false,
        label = { Icon(Icons.Filled.Add, null, modifier = Modifier.size(15.dp)) },
        onClick = {
            showDialog = true
        },
    )
}