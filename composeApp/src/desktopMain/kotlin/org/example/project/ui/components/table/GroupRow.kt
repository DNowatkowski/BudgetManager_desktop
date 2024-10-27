package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.TransactionTextField
import org.example.project.ui.components.dialogs.InputDialog

@Composable
fun GroupRow(
    group: GroupWithCategoriesAndKeywordsData,
    target: Double,
    spending: Double,
    leadingContent: @Composable () -> Unit,
    onGroupUpdated: (String, String) -> Unit,
    onCategoryAdded: (String, String) -> Unit,
    onGroupRemoved: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var dropdownExpanded by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }

    if (showAddCategoryDialog) {
        InputDialog(
            title = "Add Category",
            onConfirmed = { onCategoryAdded(group.group.id, it) },
            onDismiss = { showAddCategoryDialog = false },
            label = "Category name",
        )
    }

    if (showEditGroupDialog) {
        InputDialog(
            title = "Edit Group",
            initialText = group.group.name,
            onConfirmed = { onGroupUpdated(group.group.id, it) },
            onDismiss = { showEditGroupDialog = false },
            label = "Group name",
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
                        Text("Remove")
                    }
                    DropdownMenuItem(
                        onClick = {
                            dropdownExpanded = false
                            showEditGroupDialog = true
                        }) {
                        Icon(Icons.Filled.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit")
                    }
                    DropdownMenuItem(
                        onClick = {
                            dropdownExpanded = false
                            showAddCategoryDialog = true
                        }) {
                        Icon(Icons.Filled.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Category")
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
