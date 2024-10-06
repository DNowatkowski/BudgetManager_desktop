package org.example.project.domain.models

import database.CategoryEntity
import database.KeywordEntity
import database.TransactionEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun CategoryEntity.toDomainModel(
    keywords: List<KeywordEntity>,
    transactions: List<TransactionEntity>,
): CategoryData {
    return CategoryData(
        id = id,
        name = name,
        keywords = keywords.map { it.toDomainModel() },
        transactions = transactions.map { it.toDomainModel() },
    )
}

fun KeywordEntity.toDomainModel(): KeywordData {
    return KeywordData(
        id = id,
        keyword = keyword,
    )
}

fun TransactionEntity.toDomainModel(): TransactionData {
    return TransactionData(
        id = id,
        amount = amount,
        date = date.toLocalDateTime(),
        title = title,
    )
}

fun String.toLocalDateTime(): LocalDateTime {
    // Define the date format pattern
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    // Parse the text date to LocalDateTime
    return LocalDateTime.parse(this, formatter)
}