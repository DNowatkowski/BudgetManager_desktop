package org.example.project.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KeywordEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val categoryId: String?
)
