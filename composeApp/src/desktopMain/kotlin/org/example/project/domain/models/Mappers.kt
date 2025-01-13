package org.example.project.domain.models

import androidx.compose.ui.graphics.Color
import database.CategoryEntity
import database.GroupEntity
import database.IgnoredKeywordEntity
import database.KeywordEntity
import database.TransactionEntity
import org.example.project.data.dto.MillenniumTransactionDto
import org.example.project.data.dto.SantanderTransactionDto
import org.example.project.data.dto.TransactionDto
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupData
import org.example.project.domain.models.keyword.IgnoredKeywordData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.utils.ImageUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID

fun GroupEntity.toDomainModel() = GroupData(
    id = id,
    name = name,
    color = Color(colorCode.toInt()),
    isIncomeGroup = isIncomeGroup ?: false,
    icon = this.iconId?.let { ImageUtil.createImageVector(it) }
)

fun CategoryEntity.toDomainModel(
): CategoryData {
    return CategoryData(
        id = id,
        name = name,
        categoryGroupId = categoryGroupId,
        monthlyTarget = monthlyTarget ?: 0.0,
    )
}

fun KeywordEntity.toDomainModel(): KeywordData {
    return KeywordData(
        id = id,
        keyword = keyword,
        categoryId = categoryId,
    )
}

fun IgnoredKeywordEntity.toDomainModel(): IgnoredKeywordData {
    return IgnoredKeywordData(
        id = id,
        keyword = keyword,
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
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        LocalDate.parse(this, formatter)
    }
}

fun TransactionData.toEntity() = TransactionEntity(
    id = id,
    date = date.toString(),
    description = description,
    payee = payee,
    amount = amount,
    categoryId = categoryId,
)

fun TransactionData.toEntity(categoryId: String?) = TransactionEntity(
    id = id,
    date = date.toString(),
    description = description,
    payee = payee,
    amount = amount,
    categoryId = categoryId,
)

fun TransactionDto.toDomainModel(): TransactionData {
    return when (this) {
        is SantanderTransactionDto -> toDomainModel()
        is MillenniumTransactionDto -> toDomainModel()
        else -> throw IllegalArgumentException("Unknown transaction type")
    }
}

private fun SantanderTransactionDto.toDomainModel() = TransactionData(
    id = UUID.randomUUID().toString(),
    date = date.toLocalDate(),
    description = description,
    payee = payee,
    amount = amount.stringToDouble(),
    categoryId = null
)

private fun MillenniumTransactionDto.toDomainModel() = TransactionData(
    id = UUID.randomUUID().toString(),
    date = date.toLocalDate(),
    description = description,
    payee = payee,
    amount = if (expense.isNotEmpty()) expense.stringToDouble() else income.stringToDouble(),
    categoryId = null
)

fun String.stringToDouble(): Double {
    return try {
        this.toDouble()
    } catch (e: NumberFormatException) {
        try {
            this.replace(",", ".").toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}

fun String.stringToDoubleOrNull(): Double? {
    return try {
        this.toDouble()
    } catch (e: NumberFormatException) {
        try {
            this.replace(",", ".").toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
}

fun Double.toReadableString(withCurrency: Boolean = false): String {
    return if (withCurrency) {
        String.format("%.2f zł", this)
    } else {
        if (this == 0.0) {
            ""
        } else
            String.format("%.2f", this)
    }
}

fun LocalDate.toReadableString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return this.format(formatter)
}