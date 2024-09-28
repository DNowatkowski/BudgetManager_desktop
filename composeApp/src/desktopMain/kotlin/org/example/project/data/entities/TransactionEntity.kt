package org.example.project.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val title: String,
    val date: LocalDate,
    val categoryId: String?
)