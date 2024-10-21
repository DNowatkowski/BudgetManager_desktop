package org.example.project.ui.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerComponent.WheelDatePicker
import java.time.LocalDate

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onConfirmed: (ImportOptions) -> Unit,
) {
    var fromDate by remember { mutableStateOf(LocalDate.now().minusMonths(1)) }
    var toDate by remember { mutableStateOf(LocalDate.now()) }
    var dividedBy by remember { mutableIntStateOf(2) }
    var selectedDateOption: DateOptions by remember { mutableStateOf(DateOptions.ALL_TRANSACTIONS) }
    var selectedValueOption: ValueOptions by remember { mutableStateOf(ValueOptions.TOTAL_VALUES) }

    BudgetManagerDialog(
        title = "Import Settings",
        confirmButtonText = "Import",
        dismissButtonText = "Cancel",
        onConfirmed = {
            onConfirmed(
                ImportOptions(
                    dateFrom = if (selectedDateOption == DateOptions.DATE_RANGE) fromDate else null,
                    dateTo = if (selectedDateOption == DateOptions.DATE_RANGE) toDate else null,
                    valuesDividedBy = if (selectedValueOption == ValueOptions.VALUES_DIVIDED_BY) dividedBy else 1
                )
            )
        },
        onDismiss = onDismiss,
    ) {
        val options = listOf("Dates", "Values")
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { options.size })
        val coroutineScope = rememberCoroutineScope()

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = index == pagerState.currentPage,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size)
                ) {
                    Text(option)
                }
            }
        }
        HorizontalPager(state = pagerState, beyondViewportPageCount = 1) { page ->
            when (page) {
                0 -> DatesTab(
                    fromDate,
                    toDate,
                    { fromDate = it },
                    { toDate = it },
                    selectedDateOption
                ) {
                    selectedDateOption = it
                }

                1 -> ValuesTab(dividedBy, { dividedBy = it }, selectedValueOption) {
                    selectedValueOption = it
                }
            }
        }
    }
}

@Composable
private fun DatesTab(
    fromDate: LocalDate,
    toDate: LocalDate,
    fromDateChanged: (LocalDate) -> Unit,
    toDateChanged: (LocalDate) -> Unit,
    selectedOption: DateOptions,
    selectedOptionChanged: (DateOptions) -> Unit
) {

    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    Column {
        DateOptions.entries.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedOption == option,
                    onCheckedChange = { selectedOptionChanged(option) }
                )
                Text(option.text)
                if (option == DateOptions.DATE_RANGE) {
                    DateRangePicker(
                        fromDate = fromDate,
                        toDate = toDate,
                        enabled = selectedOption == DateOptions.DATE_RANGE,
                        fromDateChanged = fromDateChanged,
                        toDateChanged = toDateChanged,
                        expandedFrom = expandedFrom,
                        expandedTo = expandedTo,
                        onExpandedFromChange = { expandedFrom = it },
                        onExpandedToChange = { expandedTo = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun DateRangePicker(
    fromDate: LocalDate,
    toDate: LocalDate,
    enabled: Boolean,
    fromDateChanged: (LocalDate) -> Unit,
    toDateChanged: (LocalDate) -> Unit,
    expandedFrom: Boolean,
    expandedTo: Boolean,
    onExpandedFromChange: (Boolean) -> Unit,
    onExpandedToChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DatePickerDropdown(
            expanded = expandedFrom,
            date = fromDate,
            onDateChange = fromDateChanged,
            onExpandedChange = onExpandedFromChange,
            maxDate = toDate,
            enabled = enabled,
            minDate = LocalDate.MIN
        )
        Text("to:")
        DatePickerDropdown(
            expanded = expandedTo,
            date = toDate,
            onDateChange = toDateChanged,
            onExpandedChange = onExpandedToChange,
            minDate = fromDate,
            enabled = enabled,
            maxDate = LocalDate.MAX
        )
    }
}

@Composable
private fun DatePickerDropdown(
    expanded: Boolean,
    date: LocalDate,
    enabled: Boolean,
    onDateChange: (LocalDate) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    minDate: LocalDate,
    maxDate: LocalDate
) {
    Box {
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                WheelDatePicker(
                    title = "Select date:",
                    rowCount = 5,
                    doneLabelStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary),
                    startDate = date.toKotlinLocalDate(),
                    minDate = minDate.toKotlinLocalDate(),
                    maxDate = maxDate.toKotlinLocalDate(),
                    onDoneClick = {
                        onDateChange(it.toJavaLocalDate())
                        onExpandedChange(false)
                    },
                    height = 200.dp,
                    dateTextStyle = MaterialTheme.typography.labelMedium,
                )
            }
        }
        InputChip(
            selected = expanded,
            enabled = enabled,
            onClick = { onExpandedChange(true) },
            label = { Text(date.toString()) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun ValuesTab(
    dividedBy: Int,
    dividedByChanged: (Int) -> Unit,
    selectedOption: ValueOptions,
    selectedOptionChanged: (ValueOptions) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ValueOptions.entries.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedOption == option,
                    onCheckedChange = { selectedOptionChanged(option) }
                )
                Text(option.text)

                if (option == ValueOptions.VALUES_DIVIDED_BY) {
                    Box {
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            (2..4).forEach {
                                DropdownMenuItem(onClick = {
                                    dividedByChanged(it)
                                    expanded = false
                                }) {
                                    Text(it.toString())
                                }
                            }
                        }
                        InputChip(
                            selected = expanded,
                            enabled = selectedOption == ValueOptions.VALUES_DIVIDED_BY,
                            onClick = { expanded = true },
                            label = { Text(dividedBy.toString()) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

data class ImportOptions(
    val dateFrom: LocalDate?,
    val dateTo: LocalDate?,
    val valuesDividedBy: Int = 1
)

enum class DateOptions(val text: String) {
    ALL_TRANSACTIONS("All transactions"),
    DATE_RANGE("Date range")
}

enum class ValueOptions(val text: String) {
    TOTAL_VALUES("Total values"),
    VALUES_DIVIDED_BY("Values divided by:")
}