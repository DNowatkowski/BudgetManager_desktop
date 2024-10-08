package org.example.project.domain.models

import java.time.LocalDateTime

data class TransactionData(
    val id: String,
    val date: LocalDateTime,
    val amount: Double,
    val title: String,
    val recipient: String?,
)
