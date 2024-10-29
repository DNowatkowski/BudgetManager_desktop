package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
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
fun CategoriesHeaderRow() {
    Column {
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            TableCell(weight = CategoryColumn.CATEGORY.weight) {
                TableHeaderText(stringResource(Res.string.category), modifier = Modifier.padding(start = 8.dp))
            }
            VerticalDivider()
            TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
                TableHeaderText(stringResource(Res.string.actual_values))
            }
            VerticalDivider()
            TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
                TableHeaderText(stringResource(Res.string.monthly_target))
            }
        }
        HorizontalDivider()
    }
}
