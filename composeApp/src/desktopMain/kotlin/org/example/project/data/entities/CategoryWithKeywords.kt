package org.example.project.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithKeywords(
    @Embedded
    val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val keywords: List<KeywordEntity>,
)
