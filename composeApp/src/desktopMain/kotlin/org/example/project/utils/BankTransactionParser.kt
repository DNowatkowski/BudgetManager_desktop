package org.example.project.utils

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.example.project.data.dto.MillenniumTransactionDto
import org.example.project.data.dto.SantanderTransactionDto
import org.example.project.data.dto.TransactionDto
import java.io.InputStream

interface BankTransactionParser<T : TransactionDto> {
    var exceptionLineList: MutableList<Int>
    fun getSchema(): CsvSchema
    fun parseTransactions(stream: InputStream?): List<T>

}

class MillenniumBankParser(private val csvMapper: CsvMapper) :
    BankTransactionParser<MillenniumTransactionDto> {
    override var exceptionLineList: MutableList<Int> = mutableListOf()
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
        stream?.bufferedReader()?.useLines { lines ->
            val iterator = lines.iterator()
            var line = 1
            if (iterator.hasNext()) iterator.next() // Skip header
            while (iterator.hasNext()) {
                val csvLine = iterator.next()
                try {
                    val transaction = csvMapper.readerFor(MillenniumTransactionDto::class.java)
                        .with(getSchema())
                        .readValue<MillenniumTransactionDto>(csvLine)
                    list.add(transaction)
                } catch (e: JsonParseException) {
                    exceptionLineList.add(line)
                    println("JsonParseException at line $line: ${e.message}")
                } catch (e: Exception) {
                    println("Exception at line $line: ${e.message}")
                } finally {
                    line++
                }
            }
        }
        return list
    }
}

class SantanderBankParser(private val csvMapper: CsvMapper) :
    BankTransactionParser<SantanderTransactionDto> {
    override var exceptionLineList: MutableList<Int> = mutableListOf()
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
        stream?.bufferedReader()?.useLines { lines ->
            val iterator = lines.iterator()
            var line = 1
            if (iterator.hasNext()) iterator.next() // Skip header
            while (iterator.hasNext()) {
                val csvLine = iterator.next()
                try {
                    val transaction = csvMapper.readerFor(SantanderTransactionDto::class.java)
                        .with(getSchema())
                        .readValue<SantanderTransactionDto>(csvLine)
                    list.add(transaction)
                } catch (e: JsonParseException) {
                    exceptionLineList.add(line)
                    println("JsonParseException at line $line: ${e.message}")
                } catch (e: Exception) {
                    println("Exception at line $line: ${e.message}")
                } finally {
                    line++
                }
            }
        }
        return list
    }
}