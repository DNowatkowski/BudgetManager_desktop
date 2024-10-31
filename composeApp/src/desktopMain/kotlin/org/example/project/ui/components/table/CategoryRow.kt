package org.example.project.ui.components.table

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.category_name
import budgetmanager.composeapp.generated.resources.edit_category
import budgetmanager.composeapp.generated.resources.keywords
import budgetmanager.composeapp.generated.resources.remove
import budgetmanager.composeapp.generated.resources.remove_category
import budgetmanager.composeapp.generated.resources.remove_category_confirmation
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.category.CategoryWithKeywords
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.stringToDouble
import org.example.project.domain.models.stringToDoubleOrNull
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.CustomLinearProgressIndicator
import org.example.project.ui.components.TransactionTextField
import org.example.project.ui.components.chips.AddKeywordChip
import org.example.project.ui.components.chips.KeywordChip
import org.example.project.ui.components.dialogs.AlertDialog
import org.example.project.ui.components.dialogs.InputDialog
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryRow(
    category: CategoryWithKeywords,
    groupColor: Color,
    spending: Double,
    activeMonth: LocalDate,
    onCategoryUpdated: (String, String) -> Unit,
    onMonthlyTargetSet: (String, Double) -> Unit,
    onCategoryRemoved: (String) -> Unit,
    onKeywordAdded: (String, String) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    onKeywordRemoved: (KeywordData) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val progress by remember(activeMonth, category.category.monthlyTarget, spending) {
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
            title = stringResource(Res.string.edit_category),
            initialText = category.category.name,
            onConfirmed = { text ->
                onCategoryUpdated(category.category.id, text)
            },
            onDismiss = { showEditCategoryDialog = false },
            label = stringResource(Res.string.category_name),
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
                            style = MaterialTheme.typography.titleSmall
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
                            barColor = groupColor,
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    },
                    modifier = Modifier.height(50.dp)
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
                            onMonthlyTargetSet(category.category.id, targetText.stringToDouble())
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
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    if (showAlertDialog) {
                        AlertDialog(
                            title = stringResource(Res.string.remove_category),
                            text = stringResource(Res.string.remove_category_confirmation),
                            onConfirmed = {
                                onCategoryRemoved(category.category.id)
                                showAlertDialog = false
                            },
                            onDismiss = { showAlertDialog = false },
                        )
                    }

                    DropdownMenu(
                        expanded = dropDownExpanded,
                        onDismissRequest = { dropDownExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.remove)) },
                            leadingIcon = { Icon(Icons.Filled.Delete, null) },
                            onClick = {
                                showAlertDialog = true
                                dropDownExpanded = false
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.edit_category)) },
                            leadingIcon = { Icon(Icons.Filled.Edit, null) },
                            onClick = {
                                dropDownExpanded = false
                                showEditCategoryDialog = true
                            })
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
                    stringResource(Res.string.keywords) + ":",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    category.keywords.forEach { keyword ->
                        KeywordChip(
                            keyword = keyword,
                            onRemoveKeyword = { onKeywordRemoved(keyword) },
                            onKeywordUpdated = { onKeywordUpdated(it) },
                        )
                    }
                    AddKeywordChip(
                        onAddKeyword = { onKeywordAdded(category.category.id, it) },
                    )
                }
            }
        }
    }
}