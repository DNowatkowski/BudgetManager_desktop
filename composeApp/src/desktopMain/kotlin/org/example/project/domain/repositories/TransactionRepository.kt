package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.TransactionData
import java.time.LocalDate

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionData>>
    fun getTransactionsForMonth(date: LocalDate): Flow<List<TransactionData>>
    suspend fun insertTransactions(transactions: List<TransactionData>)
}