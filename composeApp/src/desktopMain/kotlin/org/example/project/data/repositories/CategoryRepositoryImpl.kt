package org.example.project.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.CategoryEntity
import database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.example.project.domain.models.CategoryWithKeywords
import org.example.project.domain.models.CategoryWithTransactions
import org.example.project.domain.repositories.CategoryRepository
import java.util.UUID

class CategoryRepositoryImpl(
    private val database: Database,
) : CategoryRepository {
    override fun getCategoriesWithKeywords(): Flow<List<CategoryWithKeywords>> =
        combine(
            database.databaseQueries.getAllCategories().asFlow().mapToList(Dispatchers.IO),
            database.databaseQueries.getAllKeywords().asFlow().mapToList(Dispatchers.IO)
        ) { categories, keywords ->
            categories.map { category ->
                val categoryKeywords = keywords.filter { it.categoryId == category.id }
                CategoryWithKeywords(category, categoryKeywords)
            }
        }


    override fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>> =
        combine(
            database.databaseQueries.getAllCategories().asFlow().mapToList(Dispatchers.IO),
            database.databaseQueries.getAllTransactions().asFlow().mapToList(Dispatchers.IO)
        ) { categories, keywords ->
            categories.map { category ->
                val categoryTransactions = keywords.filter { it.categoryId == category.id }
                CategoryWithTransactions(category, categoryTransactions)
            }
        }


    override suspend fun insertCategory(name: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.insertCategory(
                CategoryEntity(
                    UUID.randomUUID().toString(),
                    name
                )
            )
        }
    }
}