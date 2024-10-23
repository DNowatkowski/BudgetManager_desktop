package org.example.project.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.models.toDomainModel
import org.example.project.domain.models.toEntity
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.domain.repositories.TransactionRepository
import java.time.LocalDate

class TransactionRepositoryImpl(
    private val database: Database,
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<TransactionData>> {
        return database.databaseQueries.getAllTransactions().asFlow().mapToList(Dispatchers.IO)
            .map { transactions ->
                transactions.map { it.toDomainModel() }
            }
    }

    override fun getTransactionsForMonth(date: LocalDate): Flow<List<TransactionData>> {
        return database.databaseQueries.getTransactionsForMonth(date.toYearMonth()).asFlow()
            .mapToList(Dispatchers.IO)
            .map { transactions ->
                transactions.map { it.toDomainModel() }
            }
    }

    override fun getExpensesForMonth(date: LocalDate): Flow<List<TransactionData>> {
        val expenseGroupIds = database.databaseQueries.getAllCategoryGroups().executeAsList()
            .filter { it.isIncomeGroup == false }.map { it.id }
        val expenseCategoryIds = database.databaseQueries.getAllCategories().executeAsList()
            .filter { it.categoryGroupId in expenseGroupIds }.map { it.id }

        return database.databaseQueries.getTransactionsForMonth(date.toYearMonth()).asFlow()
            .mapToList(Dispatchers.IO)
            .map { transactions ->
                transactions.filter { it.categoryId in expenseCategoryIds || it.amount < 0 }
                    .map { it.toDomainModel() }
            }
    }

    override fun getTransactionsForMonthRange(
        from: LocalDate,
        to: LocalDate
    ): Flow<Map<LocalDate, List<TransactionData>>> = flow {
        val map = mutableMapOf<LocalDate, List<TransactionData>>()
        var currentDate = from
        while (currentDate.isBefore(to) || currentDate == to) {
            map[currentDate] =
                database.databaseQueries.getTransactionsForMonth(currentDate.toYearMonth())
                    .executeAsList().map { it.toDomainModel() }
            currentDate = currentDate.plusMonths(1)
        }
        emit(map.toMap())
    }

    override fun getTransactionsForDay(date: LocalDate): Flow<List<TransactionData>> {
        return database.databaseQueries.getTransactionsForDay(date.toString()).asFlow()
            .mapToList(Dispatchers.IO)
            .map { transactions ->
                transactions.map { it.toDomainModel() }
            }
    }

    override suspend fun insertTransactions(transactions: List<TransactionData>) {
        withContext(Dispatchers.IO) {
            transactions.forEach { transaction ->
                launch {
                    database.databaseQueries.insertTransaction(
                        transaction.toEntity(
                            resolveCategory(
                                transaction
                            )
                        )
                    )
                }
            }
        }
    }

    override suspend fun insertTransaction(transaction: TransactionData) {
        withContext(Dispatchers.IO) {
            launch {
                database.databaseQueries.insertTransaction(
                    transaction.toEntity()
                )
            }
        }
    }

    override suspend fun deleteTransactions(transactionIds: List<String>) {
        withContext(Dispatchers.IO) {
            transactionIds.forEach { transactionId ->
                launch {
                    database.databaseQueries.deleteTransactionById(transactionId)
                }
            }
        }
    }

    override suspend fun updateGroupForTransaction(transactionId: String, newGroupId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCategoryForTransaction(
        transactionId: String,
        newCategoryId: String?
    ) {
        withContext(Dispatchers.IO) {
            launch {
                database.databaseQueries.updateTransactionCategoryId(
                    id = transactionId,
                    categoryId = newCategoryId
                )
            }
        }
    }

    private fun resolveCategory(transaction: TransactionData): String? {
        val keywords = database.databaseQueries.getAllKeywords().executeAsList()
        return keywords.find { keyword ->
            transaction.description.contains(keyword.keyword, ignoreCase = true) ||
                    transaction.payee?.contains(keyword.keyword, ignoreCase = true) == true
        }?.categoryId

    }

    private fun LocalDate.toYearMonth(): String {
        if (monthValue < 10) {
            return "${year}-0${monthValue}"
        }
        return "${year}-${monthValue}"
    }
}

