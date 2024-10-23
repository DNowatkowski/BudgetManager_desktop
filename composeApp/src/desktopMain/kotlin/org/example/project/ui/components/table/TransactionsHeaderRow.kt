package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import org.example.project.constants.TransactionColumn
import org.example.project.ui.screens.budget.BudgetScreenViewModel

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
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TableCell(
                weight = TransactionColumn.CHECKBOX.weight,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Checkbox(
                    checked = allSelectedChecked,
                    onCheckedChange = { onAllSelectedChange(it) },
                )
            }
            VerticalDivider()
            TableCell(
                weight = TransactionColumn.DATE.weight
            ) {
                TableHeaderText("DATE")
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (sortOrder == BudgetScreenViewModel.SortOrder.DESCENDING)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            if (sortOption != BudgetScreenViewModel.TransactionSortOption.DATE) {
                                onSortOptionChanged(BudgetScreenViewModel.TransactionSortOption.DATE)
                            } else {
                                onSortOrderChanged()
                            }
                        }
                        .alpha(if (sortOption == BudgetScreenViewModel.TransactionSortOption.DATE) 1f else 0.5f)
                )
            }
            VerticalDivider()
            TableCell(
                weight = TransactionColumn.PAYEE.weight
            ) {
                TableHeaderText("PAYEE")
            }
            VerticalDivider()
            TableCell(
                weight = TransactionColumn.DESCRIPTION.weight
            ) {
                TableHeaderText("DESCRIPTION")
            }
            VerticalDivider()
            TableCell(
                weight = TransactionColumn.GROUP.weight
            ) {
                TableHeaderText(
                    "GROUP"
                )
            }
            VerticalDivider()
            TableCell(
                weight = TransactionColumn.CATEGORY.weight
            ) {
                TableHeaderText("CATEGORY")
            }
            VerticalDivider()
            TableCell(
                weight = TransactionColumn.AMOUNT.weight,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                TableHeaderText("AMOUNT")
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (sortOrder == BudgetScreenViewModel.SortOrder.DESCENDING)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            if (sortOption != BudgetScreenViewModel.TransactionSortOption.AMOUNT) {
                                onSortOptionChanged(BudgetScreenViewModel.TransactionSortOption.AMOUNT)
                            } else {
                                onSortOrderChanged()
                            }
                        }
                        .alpha(if (sortOption == BudgetScreenViewModel.TransactionSortOption.AMOUNT) 1f else 0.5f)
                )
            }
        }
        HorizontalDivider()
    }
}
