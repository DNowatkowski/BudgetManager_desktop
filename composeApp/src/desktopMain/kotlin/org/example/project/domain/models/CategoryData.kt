package org.example.project.domain.models

data class CategoryData(
    val id: String,
    val name: String,
    val keywords: List<KeywordData>,
    val transactions: List<TransactionData>,
)
