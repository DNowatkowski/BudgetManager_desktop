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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.category_name
import budgetmanager.composeapp.generated.resources.edit_category
import budgetmanager.composeapp.generated.resources.edit_name
import budgetmanager.composeapp.generated.resources.edit_target
import budgetmanager.composeapp.generated.resources.keywords
import budgetmanager.composeapp.generated.resources.monthly_target
import budgetmanager.composeapp.generated.resources.remove
import budgetmanager.composeapp.generated.resources.remove_category
import budgetmanager.composeapp.generated.resources.remove_category_confirmation
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.category.CategoryWithKeywords
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.stringToDouble
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.CustomLinearProgressIndicator
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
    spending: Double?,
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
    var showTargetInputDialog by remember { mutableStateOf(false) }
    var dropDownExpanded by remember { mutableStateOf(false) }
    val progress by remember(activeMonth, category.category.monthlyTarget, spending) {
        val target = category.category.monthlyTarget
        mutableStateOf(
            try {
                (spending?.div(target))?.toFloat()
            } catch (e: IllegalArgumentException) {
                0.0f
            }
        )
    }

    if (showTargetInputDialog) {
        InputDialog(
            title = stringResource(Res.string.monthly_target),
            initialText = category.category.monthlyTarget.toReadableString(),
            onConfirmed = { text ->
                onMonthlyTargetSet(category.category.id, text.stringToDouble())
            },
            placeholder = { Text("0,00 zł") },
            onDismiss = { showTargetInputDialog = false },
            label = "",
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
            TableCell(
                weight = CategoryColumn.EXPAND_ALL.weight,
                horizontalArrangement = Arrangement.End,
            ) {
                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            TableCell(weight = CategoryColumn.CATEGORY.weight) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        category.category.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
                    )
                    CustomLinearProgressIndicator(
                        progress = progress ?: 0.0f,
                        barColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 20.dp)
                    )

                    AnimatedVisibility(expanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                stringResource(Res.string.keywords) + ":",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            TableCell(
                weight = CategoryColumn.ACTUAL_SPENDING.weight,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = spending?.toReadableString(true).toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                )
            }
            TableCell(
                weight = CategoryColumn.MONTHLY_TARGET.weight,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = category.category.monthlyTarget.toReadableString(true),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
                )

            }
            TableCell(
                weight = CategoryColumn.ACTIONS.weight,
                horizontalArrangement = Arrangement.End
            ) {
                Box {
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
                            text = { Text(stringResource(Res.string.edit_name)) },
                            leadingIcon = { Icon(Icons.Filled.Edit, null) },
                            onClick = {
                                dropDownExpanded = false
                                showEditCategoryDialog = true
                            })
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.edit_target)) },
                            leadingIcon = { Icon(Icons.Filled.Edit, null) },
                            onClick = {
                                dropDownExpanded = false
                                showTargetInputDialog = true
                            })
                    }
                    IconButton(
                        onClick = { dropDownExpanded = true }
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}