package org.example.project.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaLocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerComponent.WheelDatePicker
import org.example.project.constants.TransactionColumn
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.stringToDouble
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.ui.components.TransactionTextField
import org.example.project.ui.components.chips.CategoryInputChip
import org.example.project.ui.components.chips.GroupInputChip
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
        mutableStateOf("0.00")
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
                Box {
                    DropdownMenu(
                        shape = MaterialTheme.shapes.large,
                        expanded = showDatePicker,
                        onDismissRequest = {
                            showDatePicker = false
                        }
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            WheelDatePicker(
                                title = "Select date:",
                                rowCount = 5,
                                doneLabelStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary),
                                onDoneClick = {
                                    date = it.toJavaLocalDate()
                                    showDatePicker = false
                                },
                                height = 200.dp,
                                dateTextStyle = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
                Checkbox(
                    checked = true,
                    onCheckedChange = null,
                )
            }

            TableCell(
                weight = TransactionColumn.DATE.weight
            ) {
                InputChip(
                    selected = showDatePicker,
                    label = { Text(date.toString(), style = MaterialTheme.typography.labelMedium) },
                    onClick = { showDatePicker = true },
                )
            }
            TableCell(
                weight = TransactionColumn.PAYEE.weight
            ) {
                BasicTextField(
                    value = payee,
                    onValueChange = { payee = it },
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .border(
                            1.dp, Color.Gray,
                            shape = MaterialTheme.shapes.small
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)

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
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    onCanceled()
                }
            ) {
                Text("Cancel")
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
                            amount = amount.stringToDouble(),
                            categoryId = selectedCategory?.id,
                        )
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Add")
            }
        }
    }
}
