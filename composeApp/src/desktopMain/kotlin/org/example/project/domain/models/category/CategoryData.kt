package org.example.project.domain.models.category

data class CategoryData(
    val id: String,
    val name: String,
    val monthlyTarget: Double = 0.00,
    var isSelected: Boolean = false,
    val categoryGroupId: String
)
