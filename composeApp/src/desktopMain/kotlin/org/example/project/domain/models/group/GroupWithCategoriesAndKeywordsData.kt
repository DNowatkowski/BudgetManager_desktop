package org.example.project.domain.models.group

import org.example.project.domain.models.category.CategoryWithKeywords

data class GroupWithCategoriesAndKeywordsData(
    val group: GroupData,
    val categories: List<CategoryWithKeywords>,
)
