package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.data.entities.CategoryWithKeywords

interface CategoryRepository {
    fun getCategoriesWithKeywords(): Flow<List<CategoryWithKeywords>>
    fun getCategoriesWithTransactions(): Flow<List<CategoryWithKeywords>>
}
