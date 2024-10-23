package org.example.project.constants

import java.time.LocalDate

data class ImportOptions(
    val dateFrom: LocalDate?,
    val dateTo: LocalDate?,
    val valuesDividedBy: Int = 1,
    val skipDuplicates: Boolean = true,
)

enum class DateOptions(val text: String) {
    ALL_TRANSACTIONS("All transactions"),
    DATE_RANGE("Date range")
}

enum class ValueOptions(val text: String) {
    TOTAL_VALUES("Total values"),
    VALUES_DIVIDED_BY("Values divided by:")
}

