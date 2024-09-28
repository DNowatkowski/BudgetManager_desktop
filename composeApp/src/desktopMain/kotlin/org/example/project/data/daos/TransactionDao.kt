package org.example.project.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.example.project.data.entities.TransactionEntity

@Dao
interface TransactionDao {
    @Insert
    fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE id = :id")
    fun getTransactionById(id: Int): Flow<TransactionEntity>

    @Query("DELETE FROM TransactionEntity WHERE id = :id")
    fun deleteTransactionById(id: Int)

    @Query("DELETE FROM TransactionEntity")
    suspend fun deleteAllTransactions()

    @Insert
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
}