package org.example.project.domain.models.group

import org.example.project.domain.models.category.CategoryData

data class GroupWithCategoryData(
    val id: String,
    val name: String,
    val categories: List<CategoryData>,
)
