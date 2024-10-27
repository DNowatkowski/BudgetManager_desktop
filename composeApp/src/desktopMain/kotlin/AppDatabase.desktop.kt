import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import database.Database
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Properties

//
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

object Sample {
    @JvmStatic
    fun main(args: Array<String>) {
        // NOTE: Connection and Statement are AutoCloseable.
        //       Don't forget to close them both in order to avoid leaks.
        try {
            DriverManager.getConnection("jdbc:sqlite:sample.db").use { connection ->
                connection.createStatement().use { statement ->
                    statement.queryTimeout = 30 // set timeout to 30 sec.

//                    statement.executeUpdate("drop table if exists person")
//                    statement.executeUpdate("create table person (id integer, name string)")
//                    statement.executeUpdate("insert into person values(1, 'leo')")
//                    statement.executeUpdate("insert into person values(2, 'yui')")
                    val rs = statement.executeQuery("select * from person")
                    while (rs.next()) {
                        // read the result set
                        println("name = " + rs.getString("name"))
                        println("id = " + rs.getInt("id"))
                    }
                }
            }
        } catch (e: SQLException) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            e.printStackTrace(System.err)
        }
    }
}