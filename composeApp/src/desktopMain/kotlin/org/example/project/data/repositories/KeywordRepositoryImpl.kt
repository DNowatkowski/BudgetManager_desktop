package org.example.project.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import database.Database
import database.IgnoredKeywordEntity
import database.KeywordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.models.keyword.IgnoredKeywordData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.toDomainModel
import org.example.project.domain.repositories.KeywordRepository
import java.util.UUID

class KeywordRepositoryImpl(
    private val database: Database,
) : KeywordRepository {


    override suspend fun insertKeyword(keywordText: String, categoryId: String) {
        withContext(Dispatchers.IO) {
            launch {
                database.databaseQueries.insertKeyword(
                    KeywordEntity(
                        id = UUID.randomUUID().toString(),
                        keyword = keywordText,
                        categoryId = categoryId,
                    )

                )
            }
        }
    }

    override suspend fun updateKeyword(keyword: KeywordData) {
        withContext(Dispatchers.IO) {
            launch {
                database.databaseQueries.updateKeywordText(
                    id = keyword.id,
                    keyword = keyword.keyword,
                )
            }
        }
    }

    override suspend fun deleteKeyword(keywordId: String) {
        withContext(Dispatchers.IO) {
            launch {
                database.databaseQueries.deleteKeywordById(keywordId)
            }
        }
    }

    override suspend fun moveKeyword(keywordId: String, newCategoryId: String) {
        withContext(Dispatchers.IO) {
            launch {
                val keyword = database.databaseQueries.getKeywordById(keywordId).executeAsOne()
                if (keyword.categoryId != newCategoryId)
                    database.databaseQueries.updateKeywordCategory(
                        id = keywordId,
                        categoryId = newCategoryId
                    )
            }
        }
    }

    override suspend fun insertIgnoredKeyword(keywordText: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.insertIgnoredKeyword(
                IgnoredKeywordEntity(
                    id = UUID.randomUUID().toString(),
                    keyword = keywordText,
                )
            )
        }
    }

    override suspend fun deleteIgnoredKeyword(keywordId: String) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.deleteIgnoredKeywordById(keywordId)
        }
    }

    override suspend fun updateIgnoredKeyword(keyword: IgnoredKeywordData) {
        withContext(Dispatchers.IO) {
            database.databaseQueries.updateIgnoredKeywordText(
                id = keyword.id,
                keyword = keyword.keyword,
            )
        }
    }

    override fun getIgnoredKeywords(): Flow<List<IgnoredKeywordData>> {
        return database.databaseQueries.getAllIgnoredKeywords().asFlow()
            .mapToList(Dispatchers.IO)
            .map { ignoredKeywords ->
                ignoredKeywords.map { it.toDomainModel() }
            }
    }
}