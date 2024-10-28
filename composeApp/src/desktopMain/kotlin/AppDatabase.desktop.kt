import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import database.Database
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Properties


//actual class DriverFactory {
//    actual fun createDriver(): SqlDriver {
//        val dbFile = File("/Users/dominiknowatkowski/Documents/BudgetManager.db")
//        val driver: SqlDriver = JdbcSqliteDriver(
//            url = "jdbc:sqlite:${dbFile.absolutePath}",
//            properties = Properties().apply { put("foreign_keys", "true") }
//        )
//
//        if (!dbFile.exists()) {
//            Database.Schema.create(driver)
//        }
//
//        return driver
//    }
//}

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(
            JdbcSqliteDriver.IN_MEMORY,
            properties = Properties().apply { put("foreign_keys", "true") })

        Database.Schema.create(driver)
        return driver
    }
}

fun createDatabase(driverFactory: DriverFactory): Database {
    return Database(driverFactory.createDriver())
}