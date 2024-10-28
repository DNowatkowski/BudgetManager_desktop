package org.example.project.constants

enum class TransactionColumn(val weight: Float) {
    CHECKBOX(0.5f),
    DATE(1f),
    PAYEE(2f),
    DESCRIPTION(3f),
    GROUP(1.5f),
    CATEGORY(1f),
    AMOUNT(1f),
}