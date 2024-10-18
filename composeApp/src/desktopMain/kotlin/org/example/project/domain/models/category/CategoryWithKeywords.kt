package org.example.project.domain.models.category

import org.example.project.domain.models.keyword.KeywordData

data class CategoryWithKeywords(
    val category: CategoryData,
    val keywords: List<KeywordData>,
)
