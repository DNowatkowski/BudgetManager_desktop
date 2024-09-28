package org.example.project.di

import org.example.project.data.entities.TransactionEntity
import org.example.project.data.repositories.CategoryRepositoryImpl
import org.example.project.data.repositories.KeywordRepositoryImpl
import org.example.project.data.repositories.TransactionRepositoryImpl
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.math.sin

fun initKoin(enableNetworkLogs: Boolean = true, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        module {
            single<CategoryRepository> { CategoryRepositoryImpl(get()) }
            single<TransactionRepository> { TransactionRepositoryImpl(get()) }
            single<KeywordRepository> { KeywordRepositoryImpl(get()) }
        }
    }

// called by iOS etc
// fun initKoin() = initKoin(enableNetworkLogs = false) {}

fun KoinApplication.Companion.start(): KoinApplication = initKoin { }