package org.example.project.domain.repositories

import org.example.project.domain.models.keyword.KeywordData

interface KeywordRepository {
    suspend fun insertKeyword(keywordText: String, categoryId: String)
    suspend fun updateKeyword(keyword: KeywordData)
    suspend fun deleteKeyword(keywordId: String)
    suspend fun moveKeyword(keywordId: String, newCategoryId: String)
}