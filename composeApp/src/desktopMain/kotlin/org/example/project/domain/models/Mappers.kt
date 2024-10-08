package org.example.project.domain.models

import database.CategoryEntity
import database.KeywordEntity
import database.TransactionEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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
        recipient = recipient,
    )
}

fun String.toLocalDateTime(): LocalDateTime {
    // Define the date format pattern
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    // Parse the text date to LocalDateTime
    return LocalDateTime.parse(this, formatter)
}

fun List<String>.toTransactionData() = TransactionData(
    id = UUID.randomUUID().toString(),
    date = this[1].toLocalDateTime(),
    title = this[2],
    recipient = this[3],
    amount = this[5].toDouble(),
)

fun TransactionData.toEntity(categoryId:String) = TransactionEntity(
    id = id,
    date = date.toString(),
    title = title,
    recipient = recipient,
    amount = amount,
    categoryId = categoryId,
)