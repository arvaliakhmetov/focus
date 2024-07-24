import android.os.Build
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import ru.aval.focus.FocusApp
import utils.DispatcherHelper


class AndroidPlatform : Platform {
    override val name: String = "1"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual val PlatformDispatcher = DispatcherHelper(
    main = Dispatchers.Main.immediate,
    io = Dispatchers.IO
)

