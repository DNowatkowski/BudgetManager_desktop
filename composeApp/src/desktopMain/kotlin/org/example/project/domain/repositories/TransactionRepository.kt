package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.data.entities.TransactionEntity

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
}