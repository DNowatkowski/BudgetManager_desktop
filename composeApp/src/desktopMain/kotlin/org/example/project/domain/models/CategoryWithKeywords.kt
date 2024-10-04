package org.example.project.domain.models

import database.CategoryEntity
import database.KeywordEntity

data class CategoryWithKeywords(
    val category: CategoryEntity,
    val keywords: List<KeywordEntity>,
)
