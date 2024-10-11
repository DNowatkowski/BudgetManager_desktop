package org.example.project.domain.models.transaction

import java.time.LocalDate

data class TransactionData(
    val id: String,
    val date: LocalDate,
    val amount: Double,
    val description: String,
    val payee: String?,
    val categoryId: String?,
    var isSelected: Boolean = false
)
