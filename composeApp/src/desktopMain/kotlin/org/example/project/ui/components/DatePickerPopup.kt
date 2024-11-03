package org.example.project.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerPopup(
    onDismissRequest: () -> Unit,
    initialDate: LocalDate,
    onDateSelected: (LocalDate?) -> Unit,
    selectableDates: CustomSelectableDates
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toMillis(),
        selectableDates = selectableDates
    )
    Popup(
        onDismissRequest = onDismissRequest,
    ) {
        Card(elevation = CardDefaults.cardElevation(4.dp)) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.height(520.dp).width(350.dp).clip(MaterialTheme.shapes.medium),
                colors = DatePickerDefaults.colors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )
        }

    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (initialDate.toMillis() != datePickerState.selectedDateMillis) {
            onDateSelected(datePickerState.selectedDateMillis?.toLocalDate())
        }
    }
}


fun LocalDate.toMillis(): Long {
    return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate {
    return LocalDate.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}


@OptIn(ExperimentalMaterial3Api::class)
class CustomSelectableDates(
    private val dateAfter: LocalDate? = null,
    private val dateBefore: LocalDate? = null,
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        if (dateAfter != null && utcTimeMillis < dateAfter.toMillis()) {
            return false
        }
        if (dateBefore != null && utcTimeMillis > dateBefore.toMillis()) {
            return false
        }
        return true
    }
}