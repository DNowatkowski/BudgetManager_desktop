package org.example.project.ui.components.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add_category
import budgetmanager.composeapp.generated.resources.category_name
import budgetmanager.composeapp.generated.resources.edit_group
import budgetmanager.composeapp.generated.resources.edit_name
import budgetmanager.composeapp.generated.resources.group_name
import budgetmanager.composeapp.generated.resources.remove
import budgetmanager.composeapp.generated.resources.select_icon
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.IconData
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.IconPicker
import org.example.project.ui.components.dialogs.InputDialog
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate

@Composable
fun GroupRow(
    group: GroupWithCategoriesAndKeywordsData,
    target: Double,
    spending: Double,
    iconsSearchText: String,
    iconsLoading: Boolean,
    icons: List<IconData>,
    activeMonth: LocalDate,
    expanded: Boolean,
    categorySpending: Map<CategoryData, Double>,
    onKeywordRemoved: (KeywordData) -> Unit,
    onSearchTextUpdated: (String) -> Unit,
    onIconClick: (String) -> Unit,
    leadingContent: @Composable () -> Unit,
    onGroupUpdated: (String, String) -> Unit,
    onCategoryRemoved: (String) -> Unit,
    onKeywordAdded: (String, String) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    onCategoryUpdated: (String, String) -> Unit,
    onCategoryAdded: (String, String) -> Unit,
    onMonthlyTargetSet: (String, Double) -> Unit,
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
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.height(52.dp)
        ) {
            TableCell(
                weight = CategoryColumn.EXPAND_ALL.weight,
            ) {
                leadingContent()
            }
            TableCell(weight = CategoryColumn.CATEGORY.weight) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    group.group.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = group.group.name,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        group.group.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            TableCell(
                weight = CategoryColumn.ACTUAL_SPENDING.weight,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = spending.toReadableString(true),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TableCell(
                weight = CategoryColumn.MONTHLY_TARGET.weight,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = target.toReadableString(true),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TableCell(
                weight = CategoryColumn.ACTIONS.weight,
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Filled.Delete, null) },
                            text = { Text(stringResource(Res.string.remove)) },
                            onClick = { onGroupRemoved(group.group.id) }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Filled.Edit, null) },
                            text = { Text(stringResource(Res.string.edit_name)) },
                            onClick = { showEditGroupDialog = true }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Filled.Add, null) },
                            text = { Text(stringResource(Res.string.add_category)) },
                            onClick = { showAddCategoryDialog = true }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Filled.Icecream, null) },
                            text = { Text(stringResource(Res.string.select_icon)) },
                            onClick = {
                                dropdownExpanded = false
                                showIconPicker = true
                            }
                        )
                    }
                    IconButton(
                        onClick = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                group.categories.forEach { category ->
                    CategoryRow(
                        onCategoryRemoved = { onCategoryRemoved(category.category.id) },
                        onCategoryUpdated = onCategoryUpdated,
                        onKeywordRemoved = onKeywordRemoved,
                        onKeywordAdded = onKeywordAdded,
                        onKeywordUpdated = onKeywordUpdated,
                        onMonthlyTargetSet = onMonthlyTargetSet,
                        category = category,
                        groupColor = group.group.color,
                        activeMonth = activeMonth,
                        spending = categorySpending[category.category]
                    )
                }
            }
        }
        HorizontalDivider()
    }
}
