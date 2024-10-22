package org.example.project.di

import DriverFactory
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import createDatabase
import org.example.project.data.repositories.CategoryRepositoryImpl
import org.example.project.data.repositories.KeywordRepositoryImpl
import org.example.project.data.repositories.TransactionRepositoryImpl
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository
import org.example.project.ui.screens.MainScreenViewModel
import org.example.project.ui.screens.budget.BudgetScreenViewModel
import org.example.project.ui.screens.categories.CategoriesScreenViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean = true) = startKoin {
    modules(appModule)
}

val appModule = module {
    single { createDatabase(DriverFactory()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<KeywordRepository> { KeywordRepositoryImpl(get()) }
    single<CsvMapper> {
        CsvMapper().apply {
            enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
            enable(CsvParser.Feature.TRIM_SPACES)
            enable(CsvParser.Feature.SKIP_EMPTY_LINES)
        }
    }
    single<CsvSchema> {
        CsvSchema.builder()
            .addColumn("PostingDate")
            .addColumn("Date")
            .addColumn("Description")
            .addColumn("Payee")
            .addColumn("AccountNumber")
            .addColumn("Amount")
            .addColumn("Balance")
            .addNumberColumn("Index")
            .build()
    }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    viewModel { CategoriesScreenViewModel(get(), get(), get()) }
    viewModel { BudgetScreenViewModel(get(), get(), get(), get()) }
    viewModel { MainScreenViewModel() }
}
