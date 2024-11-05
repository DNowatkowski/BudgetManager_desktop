package org.example.project.utils

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import org.example.project.data.dto.TransactionDto

class BankTransactionParserFactory(private val csvMapper: CsvMapper) {
    private val parsers = mapOf(
        BankType.MILLENNIUM to MillenniumBankParser(csvMapper),
        BankType.SANTANDER to SantanderBankParser(csvMapper),
    )

    fun getParser(bankType: BankType): BankTransactionParser<out TransactionDto>? {
        return parsers[bankType]
    }
}

enum class BankType(val bankName: String) {
    MILLENNIUM("Millennium Bank"),
    SANTANDER("Santander Bank")
}