package org.example.project.domain.models

import database.CategoryEntity

data class CategoryGroupData(
    val id: String,
    val name: String,
    val categories: List<CategoryData>,
)
