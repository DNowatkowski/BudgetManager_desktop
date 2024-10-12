package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.ui.screens.budgetScreen.BudgetScreenViewModel

@Composable
fun HeaderRow(
    allSelectedChecked: Boolean,
    sortOption: BudgetScreenViewModel.TransactionSortOption,
    sortOrder: BudgetScreenViewModel.SortOrder,
    onCheckboxColumnPositioned: (Int) -> Unit,
    onDateColumnPositioned: (Int) -> Unit,
    onPayeeColumnPositioned: (Int) -> Unit,
    onDescriptionColumnPositioned: (Int) -> Unit,
    onGroupColumnPositioned: (Int) -> Unit,
    onCategoryColumnPositioned: (Int) -> Unit,
    onAmountColumnPositioned: (Int) -> Unit,
    onAllSelectedChange: (Boolean) -> Unit,
    onSortOptionChanged: (BudgetScreenViewModel.TransactionSortOption) -> Unit,
    onSortOrderChanged: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = allSelectedChecked,
            onCheckedChange = { onAllSelectedChange(it) },
            modifier = Modifier.weight(0.05f)
                .padding(start = 8.dp)
                .onGloballyPositioned { onCheckboxColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Row(
            modifier = Modifier.weight(0.06f)
                .onGloballyPositioned { onDateColumnPositioned(it.size.width) }
        ) {
            Text(
                "Date",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
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
        Text(
            "Payee",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(0.2f)
                .onGloballyPositioned { onPayeeColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Text(
            "Description",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.2f)
                .onGloballyPositioned { onDescriptionColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Text(
            "Group",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.08f)
                .onGloballyPositioned { onGroupColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Text(
            "Category",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.08f)
                .onGloballyPositioned { onCategoryColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Row(
            modifier = Modifier.weight(0.08f)
                .onGloballyPositioned { onAmountColumnPositioned(it.size.width) }
                .padding(end = 8.dp)
        ) {
            Text(
                "Amount",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
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
}
