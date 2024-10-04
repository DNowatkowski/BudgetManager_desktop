package org.example.project.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import database.Database
import database.TransactionEntity
import kotlinx.coroutines.Dispatchers
import org.example.project.domain.repositories.TransactionRepository

class TransactionRepositoryImpl(
    private val database: Database,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return database.databaseQueries.getAllTransactions().asFlow().mapToList(Dispatchers.IO)
    }

}