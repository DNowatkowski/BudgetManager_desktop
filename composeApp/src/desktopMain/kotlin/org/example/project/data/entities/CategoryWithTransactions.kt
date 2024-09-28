package org.example.project.data.entities

import androidx.room.Embedded
import androidx.room.Relation


data class CategoryWithTransactions(
    @Embedded
    val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val transactions: List<TransactionEntity>,
)
