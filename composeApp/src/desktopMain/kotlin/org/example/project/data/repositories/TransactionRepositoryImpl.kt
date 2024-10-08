package org.example.project.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import database.Database
import database.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.domain.models.TransactionData
import org.example.project.domain.models.toEntity
import org.example.project.domain.models.toTransactionData
import org.example.project.domain.repositories.TransactionRepository

class TransactionRepositoryImpl(
    private val database: Database,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return database.databaseQueries.getAllTransactions().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun insertTransaction(transaction: TransactionData) {
        withContext(Dispatchers.IO) {
            resolveCategory(transaction)?.let {
                transaction.toEntity(categoryId = it)
            }?.let {
                database.databaseQueries.insertTransaction(it)
            }
        }
    }

    private fun resolveCategory(transaction: TransactionData): String? {
        val keywords = database.databaseQueries.getAllKeywords().executeAsList()
        return keywords.find { keyword ->
            transaction.title.contains(keyword.keyword, ignoreCase = true) ||
                    transaction.recipient?.contains(keyword.keyword, ignoreCase = true) == true
        }?.categoryId

    }
}

