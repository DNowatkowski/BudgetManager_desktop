package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.transaction.TransactionData
import java.time.LocalDate

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionData>>
    fun getTransactionsForMonth(date: LocalDate): Flow<List<TransactionData>>
    fun getExpensesForMonth(date: LocalDate): Flow<List<TransactionData>>
    fun getTransactionsForDay(date: LocalDate): Flow<List<TransactionData>>
    fun getTransactionsForMonthRange(
        from: LocalDate,
        to: LocalDate
    ): Flow<Map<LocalDate, List<TransactionData>>>

    suspend fun insertTransactions(transactions: List<TransactionData>)
    suspend fun insertTransaction(transaction: TransactionData)
    suspend fun deleteTransactions(transactionIds: List<String>)
    suspend fun updateGroupForTransaction(transactionId: String, newGroupId: String)
    suspend fun updateCategoryForTransaction(transactionId: String, newCategoryId: String?)
}