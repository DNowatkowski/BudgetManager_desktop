package org.example.project.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.example.project.data.entities.CategoryEntity
import org.example.project.data.entities.CategoryWithKeywords
import org.example.project.data.entities.CategoryWithTransactions

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM CategoryEntity")
    fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>>

    @Transaction
    @Query("SELECT * FROM CategoryEntity")
    fun getCategoriesWithKeyWords(): Flow<List<CategoryWithKeywords>>

    @Query("SELECT * FROM CategoryEntity")
    fun getCategories():Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Query("DELETE FROM CategoryEntity WHERE id = :id")
    suspend fun deleteCategoryById(id: Int)
}