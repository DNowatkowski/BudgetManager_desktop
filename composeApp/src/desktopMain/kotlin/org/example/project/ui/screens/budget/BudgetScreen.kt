package org.example.project.ui.screens.budget

import androidx.collection.emptyLongSet
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add_transaction
import budgetmanager.composeapp.generated.resources.delete_transaction
import budgetmanager.composeapp.generated.resources.delete_transaction_confirmation
import budgetmanager.composeapp.generated.resources.import
import budgetmanager.composeapp.generated.resources.import_exception
import budgetmanager.composeapp.generated.resources.import_exception_desc
import budgetmanager.composeapp.generated.resources.search
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import org.example.project.ui.components.VerticalScrollBar
import org.example.project.ui.components.dialogs.AlertDialog
import org.example.project.ui.components.dialogs.ImportDialog
import org.example.project.ui.components.dialogs.WarningDialog
import org.example.project.ui.components.table.AddTransactionRow
import org.example.project.ui.components.table.TransactionRow
import org.example.project.ui.components.table.TransactionsHeaderRow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate


@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetScreen(
    activeMonth: LocalDate
) {
    var showNewTransactionRow by remember { mutableStateOf(false) }

    val vm = koinViewModel<BudgetScreenViewModel>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var showAlertDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var platformFile: PlatformFile? by remember { mutableStateOf(null) }
    var showImportExceptionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.exceptionLineList) {
        showImportExceptionDialog = uiState.exceptionLineList.isNotEmpty()
    }

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
    if (showImportExceptionDialog) {
        WarningDialog(
            title = stringResource(Res.string.import_exception),
            text = stringResource(Res.string.import_exception_desc) + " ${
                uiState.exceptionLineList.joinToString()
            }",
            onDismiss = { vm.resetImportExceptions() },
        )
    }

    if (showImportDialog) {
        ImportDialog(
            ignoredKeywords = uiState.ignoredKeywords,
            onIgnoredKeywordAdded = { vm.addIgnoredKeyword(it) },
            onIgnoredKeywordRemoved = { vm.removeIgnoredKeyword(it) },
            onIgnoredKeywordUpdated = { vm.updateIgnoredKeyword(it) },
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
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.width(250.dp).height(50.dp)
            )
        }

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    MaterialTheme.shapes.small
                )
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TransactionsHeaderRow(
                allSelectedChecked = uiState.transactions.any { it.isSelected },
                onAllSelectedChange = { vm.toggleAllTransactionsSelection(it) },
                sortOption = uiState.sortOption,
                sortOrder = uiState.sortOrder,
                onSortOptionChanged = { vm.updateSortOption(it) },
                onSortOrderChanged = { vm.toggleSortOrder() }
            )
            AnimatedVisibility(showNewTransactionRow) {
                Column {
                    AddTransactionRow(
                        groups = uiState.groups,
                        onCanceled = {
                            showNewTransactionRow = false
                        },
                        onAdded = { transaction ->
                            vm.addTransaction(transaction)
                            showNewTransactionRow = false
                        },
                    )
                    HorizontalDivider()
                }
            }
            Box {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
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
                        if (index < uiState.transactions.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
                VerticalScrollBar(
                    listState = listState,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

