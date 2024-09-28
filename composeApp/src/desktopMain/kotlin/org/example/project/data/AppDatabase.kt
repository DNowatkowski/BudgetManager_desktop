package org.example.project.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.example.project.data.daos.CategoryDao
import org.example.project.data.daos.KeywordDao
import org.example.project.data.daos.TransactionDao
import org.example.project.data.entities.CategoryEntity
import org.example.project.data.entities.KeywordEntity
import org.example.project.data.entities.TransactionEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        KeywordEntity::class,

    ], version = 1, exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun keywordDao(): KeywordDao
}
