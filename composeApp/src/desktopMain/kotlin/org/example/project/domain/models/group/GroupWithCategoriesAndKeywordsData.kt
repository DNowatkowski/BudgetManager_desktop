package org.example.project.domain.models.group

import org.example.project.domain.models.category.CategoryWithKeywords

data class GroupWithCategoriesAndKeywordsData(
    val id: String,
    val name: String,
    val categories: List<CategoryWithKeywords>,
)
