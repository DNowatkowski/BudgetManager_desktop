package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add_category
import budgetmanager.composeapp.generated.resources.category_name
import budgetmanager.composeapp.generated.resources.edit
import budgetmanager.composeapp.generated.resources.edit_group
import budgetmanager.composeapp.generated.resources.group_name
import budgetmanager.composeapp.generated.resources.remove
import budgetmanager.composeapp.generated.resources.select_icon
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.IconData
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.IconPicker
import org.example.project.ui.components.TransactionTextField
import org.example.project.ui.components.dialogs.InputDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupRow(
    group: GroupWithCategoriesAndKeywordsData,
    target: Double,
    spending: Double,
    iconsSearchText: String,
    iconsLoading: Boolean,
    icons: List<IconData>,
    onSearchTextUpdated: (String) -> Unit,
    onIconClick: (String) -> Unit,
    leadingContent: @Composable () -> Unit,
    onGroupUpdated: (String, String) -> Unit,
    onCategoryAdded: (String, String) -> Unit,
    onGroupRemoved: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var dropdownExpanded by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }

    if (showAddCategoryDialog) {
        InputDialog(
            title = stringResource(Res.string.add_category),
            onConfirmed = { onCategoryAdded(group.group.id, it) },
            onDismiss = { showAddCategoryDialog = false },
            label = stringResource(Res.string.category_name),
        )
    }
    if (showIconPicker) {
        IconPicker(
            searchText = iconsSearchText,
            isLoading = iconsLoading,
            onDismissRequest = { showIconPicker = false },
            icons = icons,
            onIconClick = {
                onIconClick(it)
                showIconPicker = false
            },
            onSearchTextUpdated = onSearchTextUpdated
        )
    }
    if (showEditGroupDialog) {
        InputDialog(
            title = stringResource(Res.string.edit_group),
            initialText = group.group.name,
            onConfirmed = { onGroupUpdated(group.group.id, it) },
            onDismiss = { showEditGroupDialog = false },
            label = stringResource(Res.string.group_name),
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            ListItem(
                leadingContent = leadingContent,
                headlineContent = {
                    Text(
                        group.group.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    headlineColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
            TransactionTextField(
                value = spending.toReadableString(),
                readOnly = true,
                enabled = false,
                onValueChange = {},
                modifier = Modifier.widthIn(max = 120.dp)
            )
        }
        TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
            TransactionTextField(
                value = target.toReadableString(),
                readOnly = true,
                enabled = false,
                onValueChange = {},
                modifier = Modifier.widthIn(max = 120.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.padding(end = 8.dp)) {
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    DropdownMenuItem(onClick = { onGroupRemoved(group.group.id) }) {
                        Icon(Icons.Filled.Delete, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.remove))
                    }
                    DropdownMenuItem(
                        onClick = {
                            dropdownExpanded = false
                            showEditGroupDialog = true
                        }) {
                        Icon(Icons.Filled.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.edit))
                    }
                    DropdownMenuItem(
                        onClick = {
                            dropdownExpanded = false
                            showAddCategoryDialog = true
                        }) {
                        Icon(Icons.Filled.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.add_category))
                    }
                    DropdownMenuItem(
                        onClick = {
                            dropdownExpanded = false
                            showIconPicker = true
                        }) {
                        Icon(Icons.Filled.Icecream, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.select_icon))
                    }
                }
                IconButton(
                    onClick = { dropdownExpanded = !dropdownExpanded }
                ) {
                    Icon(Icons.Outlined.MoreVert, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
