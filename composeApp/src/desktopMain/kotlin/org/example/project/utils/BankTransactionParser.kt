package org.example.project.utils

import com.fasterxml.jackson.core.JsonParseException
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
        val list = mutableListOf<MillenniumTransactionDto>()
        csvMapper.readerFor(MillenniumTransactionDto::class.java)
            .with(getSchema().withSkipFirstDataRow(true))
            .readValues<MillenniumTransactionDto>(stream)
            .let {
                while (it.hasNext()) {
                    try {
                        list.add(it.nextValue())
                    } catch (e: JsonParseException) {
                        continue
                    }
                }
            }
        return list
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
        val list = mutableListOf<SantanderTransactionDto>()
        csvMapper.readerFor(SantanderTransactionDto::class.java)
            .with(getSchema().withSkipFirstDataRow(true))
            .readValues<SantanderTransactionDto>(stream)
            .let {
                while (it.hasNext()) {
                    try {
                        list.add(it.nextValue())
                    } catch (e: JsonParseException) {
                        return@let
                    }
                }
            }
        return list
    }
}