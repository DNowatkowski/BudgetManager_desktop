package org.example.project.ui.components.chips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.category.CategoryData

@Composable
fun CategoryInputChip(
    categoriesForGroup: List<CategoryData>,
    enabled: Boolean = true,
    selectedCategory: CategoryData?,
    onCategorySelected: (CategoryData) -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.BottomEnd) {
        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { dropdownMenuExpanded = false }
        ) {
            categoriesForGroup.forEach { category ->
                TextButton(
                    onClick = {
                        onCategorySelected(category)
                        dropdownMenuExpanded = false
                    }
                ) {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = androidx.compose.ui.Modifier.weight(1f).widthIn(10.dp))
                    if (selectedCategory == category)
                        Icon(Icons.Filled.Check, null)
                }
            }
        }
    }
    InputChip(
        selected = dropdownMenuExpanded,
        enabled = enabled,
        onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
        label = {
            Text(
                selectedCategory?.name ?: "Undefined",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}
