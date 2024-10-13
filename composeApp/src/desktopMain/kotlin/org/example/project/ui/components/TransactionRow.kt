package org.example.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.transaction.TransactionData

@Composable
fun TransactionRow(
    transaction: TransactionData,
    groups: List<GroupWithCategoryData>,
    checkBoxRowWidth: Int,
    dateRowWidth: Int,
    payeeRowWidth: Int,
    descriptionRowWidth: Int,
    groupRowWidth: Int,
    categoryRowWidth: Int,
    amountRowWidth: Int,
    onCheckedChange: (String) -> Unit,
    onCategoryReset: (String) -> Unit,
    onCategorySelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory by remember(transaction.categoryId, groups) {
        mutableStateOf(
            groups.find { group -> group.categories.any { it.id == transaction.categoryId } }?.categories?.find { it.id == transaction.categoryId }
        )
    }
    var selectedGroup by remember(selectedCategory?.categoryGroupId) {
        mutableStateOf(
            selectedCategory?.let { category ->
                groups.find { it.id == category.categoryGroupId }
            }
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Checkbox(
            checked = transaction.isSelected,
            onCheckedChange = { onCheckedChange(transaction.id) },
            modifier = Modifier
                .padding(start = 8.dp)
                .width(checkBoxRowWidth.dp)
        )
        Text(
            transaction.date.toString(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(dateRowWidth.dp)
        )
        Text(
            transaction.payee.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(payeeRowWidth.dp)
        )
        Text(
            transaction.description,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(descriptionRowWidth.dp)
        )
        Column(modifier = Modifier.width(groupRowWidth.dp)) {
            GroupInputChip(
                groups = groups,
                selectedGroup = selectedGroup,
                onGroupSelected = { group ->
                    onCategoryReset(transaction.id)
                    selectedGroup = group
                }
            )
        }
        Column(modifier = Modifier.width(categoryRowWidth.dp)) {
            CategoryInputChip(
                enabled = selectedGroup != null,
                categoriesForGroup = selectedGroup?.categories.orEmpty(),
                selectedCategory = selectedCategory,
                onCategorySelected = { category -> onCategorySelected(transaction.id, category.id) }
            )
        }

        Text(
            text = if (transaction.amount < 0) "${transaction.amount} zł" else "+${transaction.amount} zł",
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (transaction.amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(amountRowWidth.dp)
        )
    }
}
