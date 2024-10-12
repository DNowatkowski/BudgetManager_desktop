package org.example.project.ui.screens.budgetScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.ui.components.BudgetManagerDialog
import org.example.project.ui.components.CategoryInputChip
import org.example.project.ui.components.GroupInputChip
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

        val listState = rememberLazyListState()
        var showAlertDialog by remember { mutableStateOf(false) }

        if (showAlertDialog)
            BudgetManagerDialog(
                title = "Delete transaction",
                onDismiss = { showAlertDialog = false },
                onConfirmed = { vm.deleteSelectedTransactions() },
                confirmButtonText = "Delete",
                dismissButtonText = "Cancel",
                content = {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                    Text("Are you sure you want to delete selected transactions?")
                }
            )

        Column(modifier = Modifier.padding(vertical = 16.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    showNewTransactionRow = true
                }) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add transaction")
                }
                TextButton(onClick = {
                    showAlertDialog = true
                }) {
                    Icon(Icons.Filled.Delete, null)
                    Text("Delete transaction")
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = uiState.searchText,
                    shape = MaterialTheme.shapes.large,
                    onValueChange = { vm.updateSearchText(it) },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = {
                        if (uiState.searchText.isNotEmpty()) {
                            Icon(Icons.Filled.Clear, null,
                                modifier = Modifier.clickable { vm.updateSearchText("") }
                            )
                        }
                    },
                    placeholder = {
                        Text(
                            "Search",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.width(250.dp).height(50.dp)
                )
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
                onAllSelectedChange = { vm.toggleAllTransactionsSelection(it) },
                sortOption = uiState.sortOption,
                sortOrder = uiState.sortOrder,
                onSortOptionChanged = { vm.updateSortOption(it) },
                onSortOrderChanged = { vm.toggleSortOrder() }
            )
            HorizontalDivider()
            LazyColumn(
                state = listState,
            ) {
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
                        },
                        onCategoryReset = { transactionId, newGroupId ->
                            vm.resetCategoryForTransaction(transactionId, newGroupId)
                        },
                        onCategorySelected = { transactionId, newCategoryId ->
                            vm.updateCategoryForTransaction(transactionId, newCategoryId)
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = allSelectedChecked,
            onCheckedChange = { onAllSelectedChange(it) },
            modifier = Modifier.weight(0.05f)
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
        Row(
            modifier = Modifier.weight(0.08f)
                .onGloballyPositioned { onAmountColumnPositioned(it.size.width) }
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
    onCategoryReset: (String, String) -> Unit,
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
            GroupInputChip(
                groups = groups,
                selectedGroup = selectedGroup,
                onGroupSelected = {
                    onCategoryReset(transaction.id, it)
                    selectedGroup = groups.find { group -> group.id == it }
                }
            )
        }
        Column(modifier = Modifier.width(categoryRowWidth.dp)) {
            CategoryInputChip(
                categoriesForGroup = selectedGroup?.categories.orEmpty(),
                selectedCategory = selectedCategory,
                onCategorySelected = { onCategorySelected(transaction.id, it) }
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

