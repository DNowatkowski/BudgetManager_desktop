package org.example.project.domain.models

import database.CategoryEntity
import database.TransactionEntity

data class CategoryWithTransactions(
    val category: CategoryEntity,
    val transactions: List<TransactionEntity>,
)
