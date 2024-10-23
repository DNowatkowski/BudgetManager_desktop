package org.example.project.domain.models.group

import org.example.project.domain.models.category.CategoryData

data class GroupWithCategoryData(
    val group: GroupData,
    val categories: List<CategoryData>,
)
