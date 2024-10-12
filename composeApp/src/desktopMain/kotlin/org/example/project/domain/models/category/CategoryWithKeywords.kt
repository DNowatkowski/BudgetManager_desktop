package org.example.project.domain.models.category

import org.example.project.domain.models.keyword.KeywordData

data class CategoryWithKeywords(
    val id: String,
    val name: String,
    val keywords: List<KeywordData>,
)
