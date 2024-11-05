package org.example.project.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.keyword.IgnoredKeywordData
import org.example.project.domain.models.keyword.KeywordData

interface KeywordRepository {
    suspend fun insertKeyword(keywordText: String, categoryId: String)
    suspend fun updateKeyword(keyword: KeywordData)
    suspend fun deleteKeyword(keywordId: String)
    suspend fun moveKeyword(keywordId: String, newCategoryId: String)
    suspend fun insertIgnoredKeyword(keywordText: String)
    suspend fun deleteIgnoredKeyword(keywordId: String)
    suspend fun updateIgnoredKeyword(keyword: IgnoredKeywordData)
    fun getIgnoredKeywords(): Flow<List<IgnoredKeywordData>>
}