package org.example.project.CsvReader

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.coroutineScope

class CsvReader() {

    companion object {
        fun readFile(platformFile: PlatformFile): Result<List<String>> {
            val file = platformFile.file
            try {
                val rows: List<List<String>> = csvReader {
                    skipEmptyLine = true
                    escapeChar = ';'
                }.readAll(file)
                return Result.success(rows.flatten())
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
    }
}