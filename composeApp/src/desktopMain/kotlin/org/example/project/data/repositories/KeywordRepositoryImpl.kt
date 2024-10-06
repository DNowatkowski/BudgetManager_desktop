package org.example.project.data.repositories

import androidx.compose.runtime.key
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import database.Database
import database.KeywordEntity
import org.example.project.domain.models.KeywordData
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
                database.databaseQueries.updateKeyword(
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
}