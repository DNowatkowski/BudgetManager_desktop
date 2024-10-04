package org.example.project.domain.repositories

import database.KeywordEntity

interface KeywordRepository {
    suspend fun insertKeyword(keywordText: String, categoryId: String)
    suspend fun deleteKeyword(keyword: KeywordEntity)
}