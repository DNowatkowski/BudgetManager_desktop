package org.example.project.ui.components.chips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.ui.components.dialogs.InputDialog

@Composable
fun KeywordChip(
    keyword: KeywordData,
    onRemoveKeyword: (KeywordData) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog)
        InputDialog(
            title = "Edit Keyword",
            initialText = keyword.keyword,
            onConfirmed = { onKeywordUpdated(keyword.copy(keyword = it)) },
            onDismiss = { showDialog = false },
            label = "Keyword",
        )
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(onClick = { onRemoveKeyword(keyword) }) {
                Icon(Icons.Filled.Delete, null)
                Spacer(Modifier.width(8.dp))
                Text("Remove")
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    showDialog = true
                }) {
                Icon(Icons.Filled.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }
        }
        InputChip(
            selected = false,
            label = { Text(keyword.keyword, style = MaterialTheme.typography.labelMedium) },
            onClick = { expanded = true },
            modifier = modifier
        )
    }
}