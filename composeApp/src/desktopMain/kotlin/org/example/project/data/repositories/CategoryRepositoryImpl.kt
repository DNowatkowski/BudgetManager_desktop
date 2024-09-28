package org.example.project.data.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.data.AppDatabase
import org.example.project.data.entities.CategoryWithKeywords
import org.example.project.domain.repositories.CategoryRepository

class CategoryRepositoryImpl(
    val database: AppDatabase,
):CategoryRepository {
    override fun getCategoriesWithKeywords(): Flow<List<CategoryWithKeywords>> {
        TODO("Not yet implemented")
    }

    override fun getCategoriesWithTransactions(): Flow<List<CategoryWithKeywords>> {
        TODO("Not yet implemented")
    }
}