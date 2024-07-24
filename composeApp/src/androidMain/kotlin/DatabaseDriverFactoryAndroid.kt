import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.aval.focus.FocusApp
import ru.aval.focus.db.FocusDatabase


actual class DatabaseDriverFactory {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = FocusDatabase.Schema,
            context = FocusApp.context,
            name = "FocusDatabase.db"
        )
    }
}