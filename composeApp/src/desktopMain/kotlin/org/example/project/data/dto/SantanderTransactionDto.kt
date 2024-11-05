package org.example.project.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

abstract class TransactionDto(
    open val date: String,
    open val description: String,
    open val payee: String,
) {
    constructor() : this("", "", "")
}


data class SantanderTransactionDto(
    @field:JsonProperty("PostingDate") val postingDate: String,
    @field:JsonProperty("Date") override val date: String,
    @field:JsonProperty("Description") override val description: String,
    @field:JsonProperty("Payee") override val payee: String,
    @field:JsonProperty("PayeeAccountNumber") val accountNumber: String,
    @field:JsonProperty("Amount") val amount: String,
    @field:JsonProperty("Balance") val balance: String,
    @field:JsonProperty("Index") val index: Int,
) : TransactionDto(date, description, payee) {
    constructor() : this("", "", "", "", "", "", "", 0)
}


data class MillenniumTransactionDto(
    @field:JsonProperty("OwnAccountNumber") val ownAccountNumber: String,
    @field:JsonProperty("PostingDate") val postingDate: String,
    @field:JsonProperty("Date") override val date: String,
    @field:JsonProperty("Type") val type: String,
    @field:JsonProperty("PayeeAccountNumber") val payeeAccountNumber: String,
    @field:JsonProperty("Payee") override val payee: String,
    @field:JsonProperty("Description") override val description: String,
    @field:JsonProperty("Expense") val expense: String,
    @field:JsonProperty("Income") val income: String,
    @field:JsonProperty("Balance") val balance: String
) : TransactionDto(date, description, payee) {
    constructor() : this("", "", "", "", "", "", "", "", "", "")
}