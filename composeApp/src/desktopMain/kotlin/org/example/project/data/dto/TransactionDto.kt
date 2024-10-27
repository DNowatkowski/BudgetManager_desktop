package org.example.project.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionDto(
    @field:JsonProperty("PostingDate") val postingDate: String,
    @field:JsonProperty("Date") val date: String,
    @field:JsonProperty("Description") val description: String,
    @field:JsonProperty("Payee") val payee: String,
    @field:JsonProperty("AccountNumber") val accountNumber: String,
    @field:JsonProperty("Amount") val amount: String,
    @field:JsonProperty("Balance") val balance: String,
    @field:JsonProperty("Index") val index: Int,
) {
    constructor() : this("", "", "", "", "", "", "", 0)
}