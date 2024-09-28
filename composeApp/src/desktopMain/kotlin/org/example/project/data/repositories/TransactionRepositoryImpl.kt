package org.example.project.data.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.data.AppDatabase
import org.example.project.data.entities.TransactionEntity
import org.example.project.domain.repositories.TransactionRepository

class TransactionRepositoryImpl(
    val database: AppDatabase,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        TODO("Not yet implemented")
    }

}