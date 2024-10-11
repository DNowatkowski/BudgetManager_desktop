package org.example.project.domain.models.category

data class CategoryData(
    val id: String,
    val name: String,
    var isSelected: Boolean = false,
    val categoryGroupId: String
)
