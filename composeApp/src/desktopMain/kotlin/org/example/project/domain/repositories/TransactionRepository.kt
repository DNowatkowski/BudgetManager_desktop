package org.example.project.domain.repositories

import database.TransactionEntity
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.TransactionData

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun insertTransaction(transaction: TransactionData)
}