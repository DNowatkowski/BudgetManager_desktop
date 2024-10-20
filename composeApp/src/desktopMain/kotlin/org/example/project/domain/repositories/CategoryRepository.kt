package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.group.GroupWithCategoryData

interface CategoryRepository {
    fun getGroupsWithCategoriesAndKeywords(): Flow<List<GroupWithCategoriesAndKeywordsData>>
    fun getGroupsWithCategories(): Flow<List<GroupWithCategoryData>>
    suspend fun insertCategory(name: String, groupId: String)
    suspend fun insertCategoryGroup(name: String)
    suspend fun updateCategoryGroup(id: String, name: String)
    suspend fun updateCategory(id: String, name: String)
    suspend fun deleteCategoryGroup(id: String)
    suspend fun deleteCategory(id: String)
    suspend fun updateMonthlyTarget(categoryId: String, target: Double)
}
