package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.CategoryGroupData
import org.example.project.domain.models.CategoryWithKeywords
import org.example.project.domain.models.CategoryWithTransactions

interface CategoryRepository {
    fun getAllCategoriesWithData(): Flow<List<CategoryGroupData>>
    suspend fun insertCategory(name: String, groupId: String)
    suspend fun insertCategoryGroup(name: String)
    suspend fun updateCategoryGroup(id: String, name: String)
    suspend fun deleteCategoryGroup(id: String)
}
