package org.example.project.data.repositories

import androidx.compose.ui.graphics.toArgb
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.CategoryEntity
import database.Database
import database.GroupEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.example.project.domain.models.category.CategoryWithKeywords
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.toDomainModel
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.ui.screens.reports.generateRandomColor
import java.util.UUID

class CategoryRepositoryImpl(
    private val database: Database,
) : CategoryRepository {

    override fun getGroupsWithCategoriesAndKeywords(): Flow<List<GroupWithCategoriesAndKeywordsData>> {
        return combine(
            database.databaseQueries.getAllCategoryGroups().asFlow().mapToList(Dispatchers.IO),
            database.databaseQueries.getAllCategories().asFlow().mapToList(Dispatchers.IO),
            database.databaseQueries.getAllKeywords().asFlow().mapToList(Dispatchers.IO),
        ) { groups, categories, keywords ->
            groups.map { group ->
                GroupWithCategoriesAndKeywordsData(
                    group = group.toDomainModel(),
                    categories = categories.filter { it.categoryGroupId == group.id }
                        .map { category ->
                            CategoryWithKeywords(
                                category = category.toDomainModel(),
                                keywords = keywords.filter { it.categoryId == category.id }
                                    .map { it.toDomainModel() }
                            )
                        }
                )
            }
        }
    }

    override fun getGroupsWithCategories(): Flow<List<GroupWithCategoryData>> {
        return combine(
            database.databaseQueries.getAllCategoryGroups().asFlow().mapToList(Dispatchers.IO),
            database.databaseQueries.getAllCategories().asFlow().mapToList(Dispatchers.IO),
        ) { groups, categories ->
            groups.map { group ->
                GroupWithCategoryData(
                    group = group.toDomainModel(),
                    categories = categories.filter { it.categoryGroupId == group.id }
                        .map { it.toDomainModel() }
                )
            }
        }
    }

    override suspend fun insertCategory(name: String, groupId: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.insertCategory(
                CategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    monthlyTarget = 0.00,
                    categoryGroupId = groupId
                )
            )
        }
    }

    override suspend fun insertCategoryGroup(name: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.insertCategoryGroup(
                GroupEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    colorCode = generateRandomColor().toArgb().toDouble(),
                    isIncomeGroup = false
                )
            )
        }
    }

    override suspend fun updateCategoryGroup(id: String, name: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.updateCategoryGroup(id = id, name = name)
        }
    }

    override suspend fun updateCategory(id: String, name: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.updateCategory(id = id, name = name)
        }
    }

    override suspend fun deleteCategoryGroup(id: String) {
        withContext(Dispatchers.IO) {
            val categories = database.databaseQueries.getCategoriesForGroup(id).executeAsList()
            categories.forEach { category ->
                database.databaseQueries.deleteKeywordsForCategory(category.id)
                database.databaseQueries.deleteCategoryById(category.id)
            }
            database.databaseQueries.deleteGroupById(id)
        }
    }

    override suspend fun deleteCategory(id: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.deleteKeywordsForCategory(id)
            database.databaseQueries.deleteCategoryById(id)
        }
    }

    override suspend fun updateMonthlyTarget(categoryId: String, target: Double) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.updateMonthlyTarget(
                monthlyTarget = target,
                id = categoryId
            )
        }
    }
}