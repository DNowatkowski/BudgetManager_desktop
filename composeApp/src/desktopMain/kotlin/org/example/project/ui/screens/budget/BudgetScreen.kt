package org.example.project.ui.screens.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add_transaction
import budgetmanager.composeapp.generated.resources.delete_transaction
import budgetmanager.composeapp.generated.resources.delete_transaction_confirmation
import budgetmanager.composeapp.generated.resources.import
import budgetmanager.composeapp.generated.resources.search
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import org.example.project.ui.components.VerticalScrollBar
import org.example.project.ui.components.dialogs.AlertDialog
import org.example.project.ui.components.dialogs.ImportDialog
import org.example.project.ui.components.table.AddTransactionRow
import org.example.project.ui.components.table.TransactionRow
import org.example.project.ui.components.table.TransactionsHeaderRow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate

data class BudgetScreen(
    val activeMonth: LocalDate
) : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        var showNewTransactionRow by remember { mutableStateOf(false) }

        val vm = koinViewModel<BudgetScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        val listState = rememberLazyListState()
        var showAlertDialog by remember { mutableStateOf(false) }
        var showImportDialog by remember { mutableStateOf(false) }
        var platformFile: PlatformFile? by remember { mutableStateOf(null) }

        LaunchedEffect(activeMonth) {
            vm.getTransactionsForMonth(activeMonth)
        }

        LaunchedEffect(showNewTransactionRow) {
            if (showNewTransactionRow) {
                listState.animateScrollToItem(0)
            }
        }

        val launcher = rememberFilePickerLauncher(
            mode = PickerMode.Single,
            type = PickerType.File(extensions = listOf("csv", "xlsx"))
        ) { file ->
            platformFile = file
            if (file != null)
                showImportDialog = true
        }

        if (showImportDialog) {
            ImportDialog(
                onDismiss = { showImportDialog = false },
                onConfirmed = { options ->
                    vm.importFile(platformFile?.file?.inputStream(), options)
                }
            )
        }

        if (showAlertDialog)
            AlertDialog(
                title = stringResource(Res.string.delete_transaction),
                text = stringResource(Res.string.delete_transaction_confirmation),
                onDismiss = { showAlertDialog = false },
                onConfirmed = {
                    vm.deleteSelectedTransactions()
                    showAlertDialog = false
                }
            )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    launcher.launch()
                }) {
                    Icon(Icons.Filled.ImportExport, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.import))
                }
                TextButton(onClick = {
                    showNewTransactionRow = true
                }) {
                    Icon(Icons.Filled.AddCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.add_transaction))
                }
                TextButton(onClick = {
                    showAlertDialog = true
                }, enabled = uiState.transactions.any { it.isSelected }) {
                    Icon(Icons.Filled.Delete, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.delete_transaction))
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
                            stringResource(Res.string.search),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.width(250.dp).height(50.dp)
                )
            }
            TransactionsHeaderRow(
                allSelectedChecked = uiState.transactions.any { it.isSelected },
                onAllSelectedChange = { vm.toggleAllTransactionsSelection(it) },
                sortOption = uiState.sortOption,
                sortOrder = uiState.sortOrder,
                onSortOptionChanged = { vm.updateSortOption(it) },
                onSortOrderChanged = { vm.toggleSortOrder() }
            )
            Row {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        AnimatedVisibility(showNewTransactionRow) {
                            AddTransactionRow(
                                groups = uiState.groups,
                                onCanceled = {
                                    showNewTransactionRow = false
                                },
                                onAdded = { transaction ->
                                    vm.addTransaction(transaction)
                                    showNewTransactionRow = false
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
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
                            modifier = Modifier.animateItem().background(
                                if (index % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    }
                }
                VerticalScrollBar(
                    listState = listState,
                    modifier = Modifier
                )
            }
        }
    }
}

