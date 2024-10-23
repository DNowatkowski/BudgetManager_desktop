package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.constants.CategoryColumn


@Composable
fun CategoriesHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            TableHeaderText("CATEGORY", modifier = Modifier.padding(start = 8.dp))
        }
        VerticalDivider()
        TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
            TableHeaderText("ACTUAL VALUES")
        }
        VerticalDivider()
        TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
            TableHeaderText("MONTHLY TARGET")
        }
    }
}
