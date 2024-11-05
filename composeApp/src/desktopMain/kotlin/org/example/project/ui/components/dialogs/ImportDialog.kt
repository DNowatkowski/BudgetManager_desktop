package org.example.project.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.all_transactions
import budgetmanager.composeapp.generated.resources.all_transactions_desc
import budgetmanager.composeapp.generated.resources.bank_type
import budgetmanager.composeapp.generated.resources.cancel
import budgetmanager.composeapp.generated.resources.import
import budgetmanager.composeapp.generated.resources.import_settings
import budgetmanager.composeapp.generated.resources.original_values
import budgetmanager.composeapp.generated.resources.original_values_desc
import budgetmanager.composeapp.generated.resources.skip_duplicates
import budgetmanager.composeapp.generated.resources.skip_duplicates_desc
import budgetmanager.composeapp.generated.resources.to
import org.example.project.constants.DateOptions
import org.example.project.constants.ImportOptions
import org.example.project.constants.ValueOptions
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.CustomSelectableDates
import org.example.project.ui.components.DatePickerPopup
import org.example.project.utils.BankType
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onConfirmed: (ImportOptions) -> Unit,
) {
    var fromDate by remember { mutableStateOf(LocalDate.now().minusMonths(1)) }
    var toDate by remember { mutableStateOf(LocalDate.now()) }
    var dividedBy by remember { mutableFloatStateOf(2f) }
    var selectedDateOption: DateOptions by remember { mutableStateOf(DateOptions.ALL_TRANSACTIONS) }
    var selectedValueOption: ValueOptions by remember { mutableStateOf(ValueOptions.TOTAL_VALUES) }
    var skipDuplicates by remember { mutableStateOf(true) }
    var selectedBank by remember { mutableStateOf(BankType.entries.first()) }

    BudgetManagerDialog(
        title = stringResource(Res.string.import_settings),
        confirmButtonText = stringResource(Res.string.import),
        dismissButtonText = stringResource(Res.string.cancel),
        onConfirmed = {
            onConfirmed(
                ImportOptions(
                    dateFrom = if (selectedDateOption == DateOptions.DATE_RANGE) fromDate else null,
                    dateTo = if (selectedDateOption == DateOptions.DATE_RANGE) toDate else null,
                    valuesDividedBy = if (selectedValueOption == ValueOptions.VALUES_DIVIDED_BY) dividedBy.toInt() else 1,
                    skipDuplicates = skipDuplicates,
                    bankType = selectedBank
                )
            )
        },
        onDismiss = onDismiss,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.bank_type) + ":",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(100.dp))
            Box {
                var expanded by remember { mutableStateOf(false) }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    BankType.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.bankName) },
                            onClick = {
                                expanded = false
                                selectedBank = it
                            })
                    }
                }
                ListItem(
                    headlineContent = { Text(selectedBank.bankName) },
                    trailingContent = {
                        if (expanded)
                            Icon(Icons.Filled.ArrowDropUp, contentDescription = "Close")
                        else
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Open")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { expanded = !expanded }
                        .border(
                            width = 2.dp,
                            color = if (expanded) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        )
                )
//                TextField(
//                    value = selectedBank.bankName,
//                    onValueChange = {},
//                    readOnly = true,
//                    maxLines = 1,
//                    trailingIcon = {
//                        if (expanded)
//                            Icon(Icons.Filled.ArrowDropUp, contentDescription = "Close")
//                        else
//                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Open")
//                    },
//                    modifier = Modifier.clickable { expanded = !expanded }
//                )
            }
        }
        Card(
            modifier = Modifier.widthIn(min = 400.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column {
                        Text(
                            stringResource(Res.string.all_transactions),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            stringResource(Res.string.all_transactions_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = selectedDateOption == DateOptions.ALL_TRANSACTIONS,
                        onCheckedChange = {
                            selectedDateOption =
                                if (it) DateOptions.ALL_TRANSACTIONS else DateOptions.DATE_RANGE
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                AnimatedVisibility(selectedDateOption == DateOptions.DATE_RANGE) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Column {
                            var showDatePicker by remember { mutableStateOf(false) }

                            if (showDatePicker)
                                DatePickerPopup(
                                    onDismissRequest = { showDatePicker = false },
                                    initialDate = fromDate,
                                    onDateSelected = {
                                        fromDate = it
                                        showDatePicker = false
                                    },
                                    selectableDates = CustomSelectableDates(
                                        dateBefore = toDate,
                                    )
                                )

                            InputChip(
                                selected = showDatePicker,
                                onClick = { showDatePicker = true },
                                label = { Text(fromDate.toReadableString()) },

                                )
                        }
                        Text(stringResource(Res.string.to), textAlign = TextAlign.Center)
                        Column {
                            var showDatePicker by remember { mutableStateOf(false) }
                            if (showDatePicker)
                                DatePickerPopup(
                                    onDismissRequest = { showDatePicker = false },
                                    initialDate = toDate,
                                    onDateSelected = {
                                        toDate = it
                                        showDatePicker = false
                                    },
                                    selectableDates = CustomSelectableDates(
                                        dateAfter = fromDate,
                                    )
                                )
                            InputChip(
                                selected = showDatePicker,
                                onClick = { showDatePicker = true },
                                label = { Text(toDate.toReadableString()) },
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(elevation = CardDefaults.cardElevation(8.dp)) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column {
                        Text(
                            stringResource(Res.string.original_values),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            stringResource(Res.string.original_values_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = selectedValueOption == ValueOptions.TOTAL_VALUES,
                        onCheckedChange = {
                            selectedValueOption =
                                if (it) ValueOptions.TOTAL_VALUES else ValueOptions.VALUES_DIVIDED_BY
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                AnimatedVisibility(selectedValueOption == ValueOptions.VALUES_DIVIDED_BY) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text("Select the number to divide the values by:")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 4.dp, bottom = 8.dp)
                        ) {

                            (1..4).forEach { value ->
                                Text(
                                    text = value.toString(),
                                    style = if (dividedBy.toInt() == value)
                                        MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)
                                    else
                                        MaterialTheme.typography.labelMedium,
                                )
                            }

                        }
                        Slider(
                            value = dividedBy,
                            onValueChange = { dividedBy = it },
                            valueRange = 1f..4f,
                            steps = 2,
                            modifier = Modifier.fillMaxWidth().height(21.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(elevation = CardDefaults.cardElevation(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(
                        stringResource(Res.string.skip_duplicates),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        stringResource(Res.string.skip_duplicates_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = skipDuplicates,
                    onCheckedChange = {
                        skipDuplicates = it
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
