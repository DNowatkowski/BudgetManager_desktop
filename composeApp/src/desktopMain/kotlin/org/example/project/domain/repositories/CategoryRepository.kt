package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.CategoryWithKeywords
import org.example.project.domain.models.CategoryWithTransactions

interface CategoryRepository {
    fun getCategoriesWithKeywords(): Flow<List<CategoryWithKeywords>>
    fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>>
    suspend fun insertCategory(name: String)
}
