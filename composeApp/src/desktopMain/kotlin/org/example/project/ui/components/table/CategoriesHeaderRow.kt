package org.example.project.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.actual_values
import budgetmanager.composeapp.generated.resources.category
import budgetmanager.composeapp.generated.resources.monthly_target
import org.example.project.constants.CategoryColumn
import org.jetbrains.compose.resources.stringResource


@Composable
fun CategoriesHeaderRow(
    allExpanded: Boolean,
    onToggleAllExpanded: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TableCell(weight = CategoryColumn.EXPAND_ALL.weight) {
                IconButton(onClick = { onToggleAllExpanded(!allExpanded) }) {
                    if (allExpanded) {
                        Icon(
                            Icons.Default.KeyboardDoubleArrowUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    } else {
                        Icon(
                            Icons.Default.KeyboardDoubleArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
            TableCell(weight = CategoryColumn.CATEGORY.weight) {
                TableHeaderText(
                    stringResource(Res.string.category),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            TableCell(
                weight = CategoryColumn.ACTUAL_SPENDING.weight,
                horizontalArrangement = Arrangement.End
            ) {
                TableHeaderText(stringResource(Res.string.actual_values))
            }
            TableCell(
                weight = CategoryColumn.MONTHLY_TARGET.weight,
                horizontalArrangement = Arrangement.End
            ) {
                TableHeaderText(stringResource(Res.string.monthly_target))
            }
            TableCell(weight = CategoryColumn.ACTIONS.weight) {

            }
        }
        HorizontalDivider()
    }
}
