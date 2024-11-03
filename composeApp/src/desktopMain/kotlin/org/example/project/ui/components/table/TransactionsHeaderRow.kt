package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.amount
import budgetmanager.composeapp.generated.resources.category
import budgetmanager.composeapp.generated.resources.date
import budgetmanager.composeapp.generated.resources.description
import budgetmanager.composeapp.generated.resources.group
import budgetmanager.composeapp.generated.resources.payee
import org.example.project.constants.TransactionColumn
import org.example.project.ui.screens.budget.BudgetScreenViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransactionsHeaderRow(
    allSelectedChecked: Boolean,
    sortOption: BudgetScreenViewModel.TransactionSortOption,
    sortOrder: BudgetScreenViewModel.SortOrder,
    onAllSelectedChange: (Boolean) -> Unit,
    onSortOptionChanged: (BudgetScreenViewModel.TransactionSortOption) -> Unit,
    onSortOrderChanged: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TableCell(
                weight = TransactionColumn.CHECKBOX.weight,
                horizontalArrangement = Arrangement.Center,
            ) {
                Checkbox(
                    checked = allSelectedChecked,
                    onCheckedChange = { onAllSelectedChange(it) },
                )
            }
            TableCell(
                weight = TransactionColumn.DATE.weight,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight(0.8f).clip(MaterialTheme.shapes.small).clickable {
                    if (sortOption != BudgetScreenViewModel.TransactionSortOption.DATE) {
                        onSortOptionChanged(BudgetScreenViewModel.TransactionSortOption.DATE)
                    } else {
                        onSortOrderChanged()
                    }
                }
            ) {
                TableHeaderText(stringResource(Res.string.date))
                Icon(
                    imageVector = if (sortOrder == BudgetScreenViewModel.SortOrder.DESCENDING)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(if (sortOption == BudgetScreenViewModel.TransactionSortOption.DATE) 1f else 0.5f)
                )
            }
            TableCell(
                weight = TransactionColumn.PAYEE.weight
            ) {
                TableHeaderText(stringResource(Res.string.payee))
            }
            TableCell(
                weight = TransactionColumn.DESCRIPTION.weight
            ) {
                TableHeaderText(stringResource(Res.string.description))
            }
            TableCell(
                weight = TransactionColumn.GROUP.weight
            ) {
                TableHeaderText(
                    stringResource(Res.string.group),
                )
            }
            TableCell(
                weight = TransactionColumn.CATEGORY.weight
            ) {
                TableHeaderText(stringResource(Res.string.category))
            }
            TableCell(
                weight = TransactionColumn.AMOUNT.weight,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight(0.8f).clip(MaterialTheme.shapes.small).clickable {
                    if (sortOption != BudgetScreenViewModel.TransactionSortOption.AMOUNT) {
                        onSortOptionChanged(BudgetScreenViewModel.TransactionSortOption.AMOUNT)
                    } else {
                        onSortOrderChanged()
                    }
                }
            ) {
                TableHeaderText(stringResource(Res.string.amount))
                Icon(
                    imageVector = if (sortOrder == BudgetScreenViewModel.SortOrder.DESCENDING)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier

                        .alpha(if (sortOption == BudgetScreenViewModel.TransactionSortOption.AMOUNT) 1f else 0.5f)
                )
            }
        }
        HorizontalDivider()
    }
}
