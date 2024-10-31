package org.example.project.ui.components.chips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.undefined
import org.example.project.domain.models.group.GroupWithCategoryData
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupInputChip(
    groups: List<GroupWithCategoryData>,
    selectedGroup: GroupWithCategoryData?,
    onGroupSelected: (GroupWithCategoryData) -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    Box {
        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { dropdownMenuExpanded = false }
        ) {
            groups.forEach { group ->
                DropdownMenuItem(
                    leadingIcon = {
                        if (group.group.icon != null) {
                            Icon(
                                group.group.icon,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    },
                    text = {
                        Text(
                            group.group.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingIcon = {
                        if (selectedGroup?.group?.id == group.group.id) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    onClick = {
                        onGroupSelected(group)
                        dropdownMenuExpanded = false
                    }
                )
            }
        }
    }
    InputChip(
        selected = dropdownMenuExpanded,
        onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
        leadingIcon = {
            if (selectedGroup != null && selectedGroup.group.icon != null) {
                Icon(
                    selectedGroup.group.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        label = {
            Text(
                selectedGroup?.group?.name ?: stringResource(Res.string.undefined),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}