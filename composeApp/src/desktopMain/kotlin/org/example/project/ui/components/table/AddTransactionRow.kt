package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add
import budgetmanager.composeapp.generated.resources.cancel
import budgetmanager.composeapp.generated.resources.expense
import budgetmanager.composeapp.generated.resources.income
import org.example.project.constants.TransactionColumn
import org.example.project.constants.TransactionType
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.stringToDouble
import org.example.project.domain.models.toReadableString
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.ui.components.CustomSelectableDates
import org.example.project.ui.components.DatePickerPopup
import org.example.project.ui.components.TransactionTextField
import org.example.project.ui.components.chips.CategoryInputChip
import org.example.project.ui.components.chips.GroupInputChip
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate
import java.util.UUID

@Composable
fun AddTransactionRow(
    groups: List<GroupWithCategoryData>,
    onAdded: (TransactionData) -> Unit,
    onCanceled: () -> Unit,
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
        mutableStateOf("")
    }


    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TableCell(
                weight = TransactionColumn.CHECKBOX.weight,
                horizontalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = true,
                    onCheckedChange = null,
                )
            }

            TableCell(
                weight = TransactionColumn.DATE.weight
            ) {
                if (showDatePicker)
                    DatePickerPopup(
                        onDismissRequest = { showDatePicker = false },
                        onDateSelected = {
                            if (it != null) {
                                date = it
                            }
                            showDatePicker = false
                        },
                        initialDate = date,
                        selectableDates = CustomSelectableDates()
                    )
                InputChip(
                    selected = showDatePicker,
                    label = {
                        Text(
                            date.toReadableString(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    onClick = { showDatePicker = true },
                )
            }
            TableCell(
                weight = TransactionColumn.PAYEE.weight
            ) {
                TransactionTextField(
                    value = payee,
                    onValueChange = { payee = it },
                    placeholder = null,
                    suffix = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            TableCell(
                weight = TransactionColumn.DESCRIPTION.weight
            ) {
                TransactionTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = null,
                    suffix = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            TableCell(
                weight = TransactionColumn.GROUP.weight
            ) {
                GroupInputChip(
                    groups = groups,
                    selectedGroup = selectedGroup,
                    onGroupSelected = { group ->
                        selectedCategory = null
                        selectedGroup = group
                    }
                )
            }

            TableCell(
                weight = TransactionColumn.CATEGORY.weight
            ) {
                CategoryInputChip(
                    enabled = selectedGroup != null,
                    categoriesForGroup = selectedGroup?.categories.orEmpty(),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category -> selectedCategory = category }
                )
            }
            TableCell(
                weight = TransactionColumn.AMOUNT.weight,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                TransactionTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    isError = amount.toDoubleOrNull() == null,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var transactionType by remember {
                mutableStateOf(TransactionType.EXPENSE)
            }
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                SegmentedButton(
                    selected = transactionType == TransactionType.EXPENSE,
                    onClick = {
                        transactionType = TransactionType.EXPENSE
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        TransactionType.EXPENSE.ordinal,
                        TransactionType.entries.size
                    )
                ) {
                    Text(stringResource(Res.string.expense))
                }
                SegmentedButton(
                    selected = transactionType == TransactionType.INCOME,
                    onClick = {
                        transactionType = TransactionType.INCOME
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        TransactionType.INCOME.ordinal,
                        TransactionType.entries.size
                    )
                ) {
                    Text(stringResource(Res.string.income))
                }
            }

            OutlinedButton(
                onClick = {
                    onCanceled()
                }
            ) {
                Text(stringResource(Res.string.cancel))
            }
            Button(
                enabled = amount.toDoubleOrNull() != null,
                onClick = {
                    onAdded(
                        TransactionData(
                            id = UUID.randomUUID().toString(),
                            date = date,
                            payee = payee,
                            description = description,
                            amount = when (transactionType) {
                                TransactionType.EXPENSE -> -amount.stringToDouble()
                                TransactionType.INCOME -> amount.stringToDouble()
                            },
                            categoryId = selectedCategory?.id,
                        )
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(stringResource(Res.string.add))
            }
        }
    }
}

