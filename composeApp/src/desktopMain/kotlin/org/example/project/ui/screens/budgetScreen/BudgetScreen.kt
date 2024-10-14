package org.example.project.ui.screens.budgetScreen

import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.ui.components.BudgetManagerDialog
import org.example.project.ui.components.CategoryInputChip
import org.example.project.ui.components.GroupInputChip
import org.example.project.ui.components.HeaderRow
import org.example.project.ui.components.TransactionRow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate

class BudgetScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        var showNewTransactionRow by remember { mutableStateOf(false) }

        val vm = koinViewModel<BudgetScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        val listState = rememberLazyListState()
        var showAlertDialog by remember { mutableStateOf(false) }

        val launcher = rememberFilePickerLauncher(
            mode = PickerMode.Single,
            type = PickerType.File(extensions = listOf("csv", "xlsx"))
        ) { file ->
            vm.importFile(file?.file?.inputStream())
        }

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

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    launcher.launch()
                }) {
                    Icon(Icons.Filled.ArrowDropDown, null)
                    Text(" Import file")
                }
                TextButton(onClick = {
                    showNewTransactionRow = true
                }) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add transaction")
                }
                TextButton(onClick = {
                    showAlertDialog = true
                }, enabled = uiState.transactions.any { it.isSelected }) {
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
                        onCheckedChange = { id ->
                            vm.toggleTransactionSelection(id)
                        },
                        onCategoryReset = { transactionId ->
                            vm.resetCategoryForTransaction(transactionId)
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
fun AddTransactionRow(

    groups: List<GroupWithCategoryData>,
    checkBoxRowWidth: Int,
    dateRowWidth: Int,
    payeeRowWidth: Int,
    descriptionRowWidth: Int,
    groupRowWidth: Int,
    categoryRowWidth: Int,
    amountRowWidth: Int,
    modifier: Modifier = Modifier
) {
    var selectedCategory: CategoryData? by remember {
        mutableStateOf(null)
    }
    var selectedGroup: GroupWithCategoryData? by remember {
        mutableStateOf(
            null
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var date: LocalDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var payee: String by remember {
        mutableStateOf("")
    }
    var description: String by remember {
        mutableStateOf("")
    }
    var amount: String by remember {
        mutableStateOf("0.00")
    }

    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(checkBoxRowWidth.dp)
            )
            InputChip(
                selected = showDatePicker,
                label = { Text(date.toString()) },
                onClick = { showDatePicker = !showDatePicker },
                modifier = Modifier.width(dateRowWidth.dp)
            )

            BasicTextField(
                value = payee,
                onValueChange = { payee = it },
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(payeeRowWidth.dp).padding(8.dp)
                    .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
            )
            BasicTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(descriptionRowWidth.dp)
                    .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
            )
            Column(modifier = Modifier.width(groupRowWidth.dp)) {
                GroupInputChip(
                    groups = groups,
                    selectedGroup = selectedGroup,
                    onGroupSelected = { group ->
                        selectedCategory = null
                        selectedGroup = group
                    }
                )
            }
            Column(modifier = Modifier.width(categoryRowWidth.dp)) {
                CategoryInputChip(
                    enabled = selectedGroup != null,
                    categoriesForGroup = selectedGroup?.categories.orEmpty(),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category -> selectedCategory = category }
                )
            }

            BasicTextField(
                value = amount,
                onValueChange = { amount = it },
                textStyle = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(amountRowWidth.dp).padding(8.dp)
                    .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
            )
        }
        Row(modifier = Modifier.fillMaxSize()) {
            OutlinedButton(
                onClick = {

                }
            ){}
            Button(
                onClick = {

                }
            ){}
        }
    }

}

