package org.example.project.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.CategoryEntity
import database.CategoryGroupEntity
import database.Database
import database.KeywordEntity
import database.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.example.project.domain.models.CategoryGroupData
import org.example.project.domain.models.toDomainModel
import org.example.project.domain.repositories.CategoryRepository
import java.util.UUID

class CategoryRepositoryImpl(
    private val database: Database,
) : CategoryRepository {

    override fun getAllCategoriesWithData(): Flow<List<CategoryGroupData>> =
        combine(
            getAllCategoryGroups(),
            getAllCategories(),
            getAllKeywords(),
            getAllTransactions()
        ) { groups, categories, keywords, transactions ->
            mapToCategoryGroupData(groups, categories, keywords, transactions)
        }

    private fun getAllCategoryGroups() =
        database.databaseQueries.getAllCategoryGroups().asFlow().mapToList(Dispatchers.IO)

    private fun getAllCategories() =
        database.databaseQueries.getAllCategories().asFlow().mapToList(Dispatchers.IO)

    private fun getAllKeywords() =
        database.databaseQueries.getAllKeywords().asFlow().mapToList(Dispatchers.IO)

    private fun getAllTransactions() =
        database.databaseQueries.getAllTransactions().asFlow().mapToList(Dispatchers.IO)

    private fun mapToCategoryGroupData(
        groups: List<CategoryGroupEntity>,
        categories: List<CategoryEntity>,
        keywords: List<KeywordEntity>,
        transactions: List<TransactionEntity>
    ): List<CategoryGroupData> {
        return groups.map { categoryGroup ->
            val groupCategories = categories.filter { it.categoryGroupId == categoryGroup.id }
            CategoryGroupData(
                id = categoryGroup.id,
                name = categoryGroup.name,
                categories = groupCategories.map { category ->
                    val categoryKeywords = keywords.filter { it.categoryId == category.id }
                    val categoryTransactions = transactions.filter { it.categoryId == category.id }
                    category.toDomainModel(categoryKeywords, categoryTransactions)
                }
            )
        }
    }


    override suspend fun insertCategory(name: String, groupId: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.insertCategory(
                CategoryEntity(
                    UUID.randomUUID().toString(),
                    name,
                    groupId
                )
            )
        }
    }

    override suspend fun insertCategoryGroup(name: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.insertCategoryGroup(
                CategoryGroupEntity(
                    UUID.randomUUID().toString(),
                    name
                )
            )
        }
    }

    override suspend fun updateCategoryGroup(id: String, name: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.updateCategoryGroup(id = id, name = name)
        }
    }

    override suspend fun deleteCategoryGroup(id: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.deleteCategoryGroupWithDependencies(id)
        }
    }
}