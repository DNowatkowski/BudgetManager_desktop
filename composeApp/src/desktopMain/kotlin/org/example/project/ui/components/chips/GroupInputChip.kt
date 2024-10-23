package org.example.project.ui.components.chips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.group.GroupWithCategoryData

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
                TextButton(
                    onClick = {
                        onGroupSelected(group)
                        dropdownMenuExpanded = false
                    }
                ) {
                    Text(
                        group.group.name,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f).widthIn(10.dp))
                    if (selectedGroup == group)
                        Icon(Icons.Filled.Check, null)
                }
            }
        }
    }
    InputChip(
        selected = dropdownMenuExpanded,
        onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
        colors = InputChipDefaults.inputChipColors().copy(
            containerColor = selectedGroup?.group?.color?.copy(alpha = 0.6f) ?: Color.Unspecified
        ),
        label = {
            Text(
                selectedGroup?.group?.name ?: "Undefined",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}