package org.example.project.domain.models

import java.time.LocalDate

data class TransactionData(
    val id: String,
    val date: LocalDate,
    val amount: Double,
    val description: String,
    val payee: String?,
    val categoryId: String?,
)
