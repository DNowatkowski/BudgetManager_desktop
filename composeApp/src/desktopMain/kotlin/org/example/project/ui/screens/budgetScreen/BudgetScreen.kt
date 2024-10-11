package org.example.project.ui.screens.budgetScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.transaction.TransactionData
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

class BudgetScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        var showNewTransactionRow by remember { mutableStateOf(false) }

        val vm = koinViewModel<BudgetScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        var checkboxColumnWidth by remember { mutableIntStateOf(0) }
        var dateColumnWidth by remember { mutableIntStateOf(0) }
        var payeeColumnWidth by remember { mutableIntStateOf(0) }
        var descriptionColumnWidth by remember { mutableIntStateOf(0) }
        var groupColumnWidth by remember { mutableIntStateOf(0) }
        var categoryColumnWidth by remember { mutableIntStateOf(0) }
        var amountColumnWidth by remember { mutableIntStateOf(0) }

        Column(modifier = Modifier.padding(vertical = 16.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    showNewTransactionRow = true
                }) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add transaction")
                }
            }
            HorizontalDivider()
            HeaderRow(
                allSelectedChecked = uiState.transactions.any { it.isSelected },
                onCheckboxColumnPositioned = { checkboxColumnWidth = it },
                onDateColumnPositioned = { dateColumnWidth = it },
                onPayeeColumnPositioned = { payeeColumnWidth = it },
                onDescriptionColumnPositioned = { descriptionColumnWidth = it },
                onGroupColumnPositioned = { groupColumnWidth = it },
                onCategoryColumnPositioned = { categoryColumnWidth = it },
                onAmountColumnPositioned = { amountColumnWidth = it },
                onAllSelectedChange = { vm.toggleAllTransactionsSelection(it) }
            )
            HorizontalDivider()
            LazyColumn {
                items(uiState.transactions.size) { index ->
                    val transaction = uiState.transactions[index]
                    TransactionRow(
                        transaction = transaction,
                        groups = uiState.groups,
                        checkBoxRowWidth = checkboxColumnWidth,
                        dateRowWidth = dateColumnWidth,
                        payeeRowWidth = payeeColumnWidth,
                        descriptionRowWidth = descriptionColumnWidth,
                        groupRowWidth = groupColumnWidth,
                        categoryRowWidth = categoryColumnWidth,
                        amountRowWidth = amountColumnWidth,
                        onCheckedChange = { id ->
                            vm.toggleTransactionSelection(id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    allSelectedChecked: Boolean,
    onCheckboxColumnPositioned: (Int) -> Unit,
    onDateColumnPositioned: (Int) -> Unit,
    onPayeeColumnPositioned: (Int) -> Unit,
    onDescriptionColumnPositioned: (Int) -> Unit,
    onGroupColumnPositioned: (Int) -> Unit,
    onCategoryColumnPositioned: (Int) -> Unit,
    onAmountColumnPositioned: (Int) -> Unit,
    onAllSelectedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = allSelectedChecked,
            onCheckedChange = { onAllSelectedChange(it) },
            modifier = Modifier.weight(0.05f)
                .onGloballyPositioned { onCheckboxColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Text(
            "Date",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(0.08f)
                .onGloballyPositioned { onDateColumnPositioned(it.size.width) }
        )
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
            modifier = Modifier.weight(0.1f)
                .onGloballyPositioned { onGroupColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Text(
            "Category",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.1f)
                .onGloballyPositioned { onCategoryColumnPositioned(it.size.width) }
        )
        VerticalDivider()
        Text(
            "Amount",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.08f)
                .onGloballyPositioned { onAmountColumnPositioned(it.size.width) }
        )
    }
}

@Composable
fun TransactionRow(
    transaction: TransactionData,
    groups: List<GroupWithCategoryData> = emptyList(),
    checkBoxRowWidth: Int,
    dateRowWidth: Int,
    payeeRowWidth: Int,
    descriptionRowWidth: Int,
    groupRowWidth: Int,
    categoryRowWidth: Int,
    amountRowWidth: Int,
    onCheckedChange: (String) -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Checkbox(
            checked = transaction.isSelected,
            onCheckedChange = { onCheckedChange(transaction.id) },
            modifier = Modifier.width(checkBoxRowWidth.dp)
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
            InputChip(
                selected = dropdownMenuExpanded,
                onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
                label = {

                },
            )
        }
        Column(modifier = Modifier.width(categoryRowWidth.dp)) {
            CategoryInputChip(
                categoriesForGroup = emptyList(),
                activeCategory = null,
                onCategorySelected = { }
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

@Composable
fun CategoryInputChip(
    categoriesForGroup: List<CategoryData>,
    activeCategory: CategoryData?,
    onCategorySelected: (String) -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    Box {
        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { dropdownMenuExpanded = false }
        ) {
            categoriesForGroup.forEach { category ->
                TextButton(
                    onClick = {
                        onCategorySelected(category.id)
                        dropdownMenuExpanded = false
                    }
                ) {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    InputChip(
        selected = dropdownMenuExpanded,
        onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
        label = {
            Text(
                activeCategory?.name.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}