package org.example.project.domain.repositories

import org.example.project.data.entities.KeywordEntity

interface KeywordRepository {
    suspend fun insertKeyword(keywordText: String, categoryId: String)
    suspend fun deleteKeyword(keyword: KeywordEntity)
}