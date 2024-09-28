package org.example.project.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.data.AppDatabase
import org.example.project.data.daos.KeywordDao
import org.example.project.data.entities.KeywordEntity
import org.example.project.domain.repositories.KeywordRepository
import java.util.UUID

class KeywordRepositoryImpl(
    database: AppDatabase,
) : KeywordRepository {

    private val keywordDao: KeywordDao = database.keywordDao()

    override suspend fun insertKeyword(keywordText: String, categoryId: String) {
        withContext(Dispatchers.IO) {
            launch {
                keywordDao.insertKeyword(
                    KeywordEntity(
                        id = UUID.randomUUID().toString(),
                        text = keywordText,
                        categoryId = categoryId
                    )
                )
            }
        }
    }

    override suspend fun deleteKeyword(keyword: KeywordEntity) {
        withContext(Dispatchers.IO) {
            launch {
                keywordDao.deleteKeyword(keyword)
            }
        }
    }
}