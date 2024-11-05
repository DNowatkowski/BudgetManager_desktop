package org.example.project.utils

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.example.project.data.dto.MillenniumTransactionDto
import org.example.project.data.dto.SantanderTransactionDto
import org.example.project.data.dto.TransactionDto
import java.io.InputStream

interface BankTransactionParser<T : TransactionDto> {
    fun getSchema(): CsvSchema
    fun parseTransactions(stream: InputStream?): List<T>
}

class MillenniumBankParser(private val csvMapper: CsvMapper) :
    BankTransactionParser<MillenniumTransactionDto> {
    override fun getSchema(): CsvSchema =
        CsvSchema.builder()
            .addColumn("OwnAccountNumber")
            .addColumn("PostingDate")
            .addColumn("Date")
            .addColumn("Type")
            .addColumn("PayeeAccountNumber")
            .addColumn("Payee")
            .addColumn("Description")
            .addColumn("Expense")
            .addColumn("Income")
            .addColumn("Balance")
            .build()

    override fun parseTransactions(stream: InputStream?): List<MillenniumTransactionDto> {
        return csvMapper.readerFor(MillenniumTransactionDto::class.java)
            .with(getSchema().withSkipFirstDataRow(true))
            .readValues<MillenniumTransactionDto>(stream)
            .readAll()
    }
}

class SantanderBankParser(private val csvMapper: CsvMapper) :
    BankTransactionParser<SantanderTransactionDto> {
    override fun getSchema(): CsvSchema =
        CsvSchema.builder()
            .addColumn("PostingDate")
            .addColumn("Date")
            .addColumn("Description")
            .addColumn("Payee")
            .addColumn("PayeeAccountNumber")
            .addColumn("Amount")
            .addColumn("Balance")
            .addNumberColumn("Index")
            .build()

    override fun parseTransactions(stream: InputStream?): List<SantanderTransactionDto> {
        return csvMapper.readerFor(SantanderTransactionDto::class.java)
            .with(getSchema().withSkipFirstDataRow(true))
            .readValues<SantanderTransactionDto>(stream)
            .readAll()
    }
}