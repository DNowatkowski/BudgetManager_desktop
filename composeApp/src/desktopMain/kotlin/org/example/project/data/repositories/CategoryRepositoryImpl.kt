package org.example.project.data.repositories

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
                    id = group.id,
                    name = group.name,
                    categories = categories.filter { it.categoryGroupId == group.id }
                        .map { category ->
                            CategoryWithKeywords(
                                id = category.id,
                                name = category.name,
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
                    id = group.id,
                    name = group.name,
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
                GroupEntity(
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