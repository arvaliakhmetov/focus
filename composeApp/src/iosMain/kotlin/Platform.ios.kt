import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.asTimeSource
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.date
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UIKit.UIDevice
import ru.aval.focus.db.FocusDatabase
import utils.DispatcherHelper

class IOSPlatform: Platform {
    override val name: String = "2"
}

actual fun getPlatform(): Platform = IOSPlatform()

actual val PlatformDispatcher = DispatcherHelper(
    main = Dispatchers.Main.immediate,
    io = Dispatchers.Default
)

actual class DatabaseDriverFactory actual constructor() {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = FocusDatabase.Schema,
            name = "FocusDatabase.db"
        )
    }
}
