import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import database.Database
import java.io.File
import java.util.Properties


actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("/Users/dominiknowatkowski/Documents/BudgetManager/BudgetManager.db")
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            properties = Properties().apply { put("foreign_keys", "true") }
        )

        if (!dbFile.exists()) {
            Database.Schema.create(driver)
        } else {
//            if (Database.Schema.version < 2) {
//                driver.execute(null, "CREATE TABLE IgnoredKeywordEntity (id TEXT PRIMARY KEY, keyword TEXT NOT NULL);", 0)
//                driver.execute(null, "PRAGMA user_version = 2;", 0)
//            }
        }

        return driver
    }
}

//actual class DriverFactory {
//    actual fun createDriver(): SqlDriver {
//        val driver: SqlDriver = JdbcSqliteDriver(
//            JdbcSqliteDriver.IN_MEMORY,
//            properties = Properties().apply { put("foreign_keys", "true") })
//
//        Database.Schema.create(driver)
//        return driver
//    }
//}

fun createDatabase(driverFactory: DriverFactory): Database {
    return Database(driverFactory.createDriver())
}