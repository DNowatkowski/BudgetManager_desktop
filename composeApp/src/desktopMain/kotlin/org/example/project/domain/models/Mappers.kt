package org.example.project.domain.models

import database.CategoryEntity
import database.KeywordEntity
import database.TransactionEntity
import org.example.project.data.repositories.TransactionDto
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.transaction.TransactionData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID

fun CategoryEntity.toDomainModel(
): CategoryData {
    return CategoryData(
        id = id,
        name = name,
        categoryGroupId = categoryGroupId,
    )
}

fun KeywordEntity.toDomainModel(): KeywordData {
    return KeywordData(
        id = id,
        keyword = keyword,
        categoryId = categoryId,
    )
}

fun TransactionEntity.toDomainModel(
): TransactionData {
    return TransactionData(
        id = id,
        amount = amount,
        date = date.toLocalDate(),
        description = description,
        payee = payee,
        categoryId = categoryId,
    )
}

fun String.toLocalDate(): LocalDate {
    // Define the date format pattern
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        LocalDate.parse(this, formatter)
    }
}

fun TransactionData.toEntity(categoryId: String?) = TransactionEntity(
    id = id,
    date = date.toString(),
    description = description,
    payee = payee,
    amount = amount,
    categoryId = categoryId,
)

fun TransactionDto.toDomainModel() = TransactionData(
    id = UUID.randomUUID().toString(),
    date = date.toLocalDate(),
    description = description,
    payee = payee,
    amount =
    try {
        amount.replace(",", ".").toDouble()
    } catch (e: NumberFormatException) {
        0.00
    },
    categoryId = null
)

