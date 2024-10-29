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
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add_keyword
import budgetmanager.composeapp.generated.resources.keyword
import org.example.project.ui.components.dialogs.InputDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddKeywordChip(
    onAddKeyword: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        InputDialog(
            title = stringResource(Res.string.add_keyword),
            onConfirmed = onAddKeyword,
            onDismiss = { showDialog = false },
            label = stringResource(Res.string.keyword),
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