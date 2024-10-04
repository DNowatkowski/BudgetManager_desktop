package org.example.project.di

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import DriverFactory
import createDatabase
import org.example.project.data.repositories.CategoryRepositoryImpl
import org.example.project.data.repositories.KeywordRepositoryImpl
import org.example.project.data.repositories.TransactionRepositoryImpl
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository
import org.example.project.ui.screens.keywords.KeywordsScreenViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean = true) = startKoin {
    modules(appModule)
}

val appModule = module {
    single{ createDatabase(DriverFactory()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<KeywordRepository> { KeywordRepositoryImpl(get()) }
    single<CsvReader> {
        csvReader {
            delimiter = ';'
            skipEmptyLine = true
        }
    }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    viewModel { KeywordsScreenViewModel(get(), get(), get(), get()) }
}