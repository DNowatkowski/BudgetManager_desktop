package org.example.project.ui.screens.keywords

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.category.CategoryWithKeywords
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.stringToDouble
import org.example.project.domain.models.stringToDoubleOrNull
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.CustomLinearProgressIndicator
import org.example.project.ui.components.DateSwitcher
import org.example.project.ui.components.chips.AddKeywordChip
import org.example.project.ui.components.dialogs.BudgetManagerDialog
import org.example.project.ui.components.dialogs.InputDialog
import org.example.project.ui.components.chips.KeywordChip
import org.example.project.ui.components.TransactionTextField
import org.example.project.ui.components.table.TableCell
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate


class CategoriesScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<CategoriesScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        val showDialog = remember { mutableStateOf(false) }

        if (showDialog.value) {
            InputDialog(
                title = "Add group",
                onConfirmed = { text -> vm.addGroup(text) },
                onDismiss = { showDialog.value = false },
                label = "Group name"
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize()
        ) {
            DateSwitcher(
                activeMonth = uiState.activeMonth,
                onNextMonth = { vm.nextMonth() },
                onPreviousMonth = { vm.previousMonth() },
            )
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TextButton(
                    onClick = { showDialog.value = true }
                ) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add group")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            TargetHeaderRow()
            HorizontalDivider()
            uiState.categoryGroupsWithKeywords.forEach { group ->
                GroupRow(
                    vm = vm,
                    group = group,
                    spending = uiState.groupSpending[group.group] ?: 0.0,
                    target = uiState.groupTargets[group.group] ?: 0.0
                )
                group.categories.forEach { category ->
                    CategoryRow(
                        vm = vm,
                        category = category,
                        activeMonth = uiState.activeMonth,
                        spending = uiState.categorySpending[category.category] ?: 0.0
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            Text(
                "Category",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        VerticalDivider()
        TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
            Text(
                "Actual spending",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        VerticalDivider()
        TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
            Text(
                "Monthly target",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun GroupRow(
    vm: CategoriesScreenViewModel,
    group: GroupWithCategoriesAndKeywordsData,
    target: Double,
    spending: Double,
) {

    var dropdownExpanded by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }

    if (showAddCategoryDialog) {
        InputDialog(
            title = "Add Category",
            onConfirmed = { text -> vm.addCategory(text, group.group.id) },
            onDismiss = { showAddCategoryDialog = false },
            label = "Category name",
        )
    }

    if (showEditGroupDialog) {
        InputDialog(
            title = "Edit Group",
            initialText = group.group.name,
            onConfirmed = { text -> vm.updateGroup(text, group.group.id) },
            onDismiss = { showEditGroupDialog = false },
            label = "Group name",
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            ListItem(
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
            Box {
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    DropdownMenuItem(onClick = { vm.removeGroup(group.group.id) }) {
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
            }
            IconButton(
                onClick = { dropdownExpanded = !dropdownExpanded }
            ) {
                Icon(Icons.Outlined.MoreVert, null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryRow(
    vm: CategoriesScreenViewModel,
    category: CategoryWithKeywords,
    spending: Double,
    activeMonth: LocalDate
) {
    var expanded by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val progress by remember(activeMonth, category.category.monthlyTarget) {
        val target = category.category.monthlyTarget
        mutableStateOf(
            try {
                (spending / target).toFloat()
            } catch (e: IllegalArgumentException) {
                0.0f
            }
        )
    }

    if (showEditCategoryDialog) {
        InputDialog(
            title = "Edit Category",
            initialText = category.category.name,
            onConfirmed = { text ->
                vm.updateCategory(
                    name = text,
                    categoryId = category.category.id
                )
            },
            onDismiss = { showEditCategoryDialog = false },
            label = "Category name",
        )
    }

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            .clickable { expanded = !expanded }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TableCell(weight = CategoryColumn.CATEGORY.weight) {
                ListItem(
                    headlineContent = {
                        Text(
                            category.category.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    leadingContent = {
                        if (expanded)
                            Icon(Icons.Filled.KeyboardArrowUp, null)
                        else
                            Icon(Icons.Filled.KeyboardArrowDown, null)
                    },
                    supportingContent = {
                        CustomLinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    },
                    modifier = Modifier.height(60.dp)
                )
            }
            TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
                TransactionTextField(
                    value = spending.toReadableString(),
                    readOnly = true,
                    enabled = false,
                    onValueChange = {},
                    modifier = Modifier.padding(2.dp).widthIn(max = 120.dp)
                )
            }
            TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {

                var targetText by remember { mutableStateOf("") }
                var showSaveButton by remember { mutableStateOf(false) }
                var isError by remember { mutableStateOf(false) }

                LaunchedEffect(targetText) {
                    showSaveButton =
                        targetText != category.category.monthlyTarget.toReadableString()
                }
                TransactionTextField(
                    value = targetText,
                    onValueChange = {
                        targetText = it
                        isError = it.stringToDoubleOrNull() == null
                    },
                    isError = isError,
                    modifier = Modifier
                        .padding(2.dp)
                        .onFocusEvent {
                            if (!it.isFocused && !showSaveButton) {
                                targetText = category.category.monthlyTarget.toReadableString()
                                showSaveButton = false
                            }
                        }
                        .widthIn(max = 120.dp)
                )
                AnimatedVisibility(showSaveButton) {
                    IconButton(
                        onClick = {
                            vm.setMonthlyTarget(
                                categoryId = category.category.id,
                                target = targetText.stringToDouble()
                            )
                            showSaveButton = false
                        },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !isError
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    if (showAlertDialog) {
                        BudgetManagerDialog(
                            title = "Delete transaction",
                            onDismiss = { showAlertDialog = false },
                            onConfirmed = { vm.removeCategory(categoryId = category.category.id) },
                            confirmButtonText = "Delete",
                            dismissButtonText = "Cancel",
                            content = {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text("Are you sure you want to delete category?")
                            }
                        )
                    }
                    DropdownMenu(
                        expanded = dropDownExpanded,
                        onDismissRequest = { dropDownExpanded = false },
                    ) {
                        DropdownMenuItem(onClick = {
                            showAlertDialog = true
                            dropDownExpanded = false
                        }) {
                            Icon(Icons.Filled.Delete, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Remove")
                        }
                        DropdownMenuItem(
                            onClick = {
                                dropDownExpanded = false
                                showEditCategoryDialog = true
                            }) {
                            Icon(Icons.Filled.Edit, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit Category")
                        }
                    }
                    IconButton(
                        onClick = { dropDownExpanded = true }
                    ) {
                        Icon(Icons.Filled.MoreVert, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
        AnimatedVisibility(expanded) {
            Column(
                modifier = Modifier.padding(start = 55.dp),
            ) {
                Text(
                    "Keywords:",

                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    category.keywords.forEach { keyword ->
                        KeywordChip(
                            keyword = keyword,
                            onRemoveKeyword = { vm.removeKeyword(keyword) },
                            onKeywordUpdated = { vm.updateKeyword(it) },
                        )
                    }
                    AddKeywordChip(
                        onAddKeyword = { vm.addKeyword(it, category.category.id) },
                    )
                }
            }
        }
    }
}