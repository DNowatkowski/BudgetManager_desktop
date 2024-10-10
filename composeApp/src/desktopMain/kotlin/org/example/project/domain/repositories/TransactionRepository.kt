package org.example.project.domain.repositories

import database.TransactionEntity
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.TransactionData
import java.time.LocalDate
import java.time.Month

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionData>>
    fun getTransactionsForMonth(date: LocalDate): Flow<List<TransactionData>>
    suspend fun insertTransactions(transactions: List<TransactionData>)
}