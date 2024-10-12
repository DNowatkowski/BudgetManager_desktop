package org.example.project.data.repositories

import database.Database
import database.KeywordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.models.keyword.KeywordData
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
}