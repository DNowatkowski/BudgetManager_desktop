package org.example.project.ui.components.chips

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.edit
import budgetmanager.composeapp.generated.resources.edit_keyword
import budgetmanager.composeapp.generated.resources.keyword
import budgetmanager.composeapp.generated.resources.remove
import org.example.project.domain.models.keyword.IgnoredKeywordData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.ui.components.dialogs.InputDialog
import org.jetbrains.compose.resources.stringResource

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
            title = stringResource(Res.string.edit_keyword),
            initialText = keyword.keyword,
            onConfirmed = { onKeywordUpdated(keyword.copy(keyword = it)) },
            onDismiss = { showDialog = false },
            label = stringResource(Res.string.keyword),
        )
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.remove)) },
                leadingIcon = { Icon(Icons.Filled.Delete, null) },
                onClick = { onRemoveKeyword(keyword) })

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.edit)) },
                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                onClick = {
                    expanded = false
                    showDialog = true
                })
        }
        InputChip(
            selected = false,
            label = { Text(keyword.keyword, style = MaterialTheme.typography.labelMedium) },
            onClick = { expanded = true },
            modifier = modifier
        )
    }
}

@Composable
fun KeywordChip(
    keyword: IgnoredKeywordData,
    onRemoveKeyword: (IgnoredKeywordData) -> Unit,
    onKeywordUpdated: (IgnoredKeywordData) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog)
        InputDialog(
            title = stringResource(Res.string.edit_keyword),
            initialText = keyword.keyword,
            onConfirmed = { onKeywordUpdated(keyword.copy(keyword = it)) },
            onDismiss = { showDialog = false },
            label = stringResource(Res.string.keyword),
        )
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.remove)) },
                leadingIcon = { Icon(Icons.Filled.Delete, null) },
                onClick = { onRemoveKeyword(keyword) })

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.edit)) },
                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                onClick = {
                    expanded = false
                    showDialog = true
                })
        }
        InputChip(
            selected = false,
            label = { Text(keyword.keyword, style = MaterialTheme.typography.labelMedium) },
            onClick = { expanded = true },
            modifier = modifier
        )
    }
}