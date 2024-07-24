import app.cash.sqldelight.db.SqlDriver
import repository.database.MainDatabaseRepository
import repository.network.WsClient
import org.koin.dsl.module
import utils.DispatcherHelper

interface Platform {
    val name: String
}

fun appModule() = listOf(dbModule,wsModule)

val wsModule = module {
    single<WsClient> { WsClient() }
}
val dbModule = module {
    single<MainDatabaseRepository> { MainDatabaseRepository(DatabaseDriverFactory().createDriver()) }
}

expect fun getPlatform(): Platform

expect val PlatformDispatcher: DispatcherHelper

expect class DatabaseDriverFactory() {

    fun createDriver(): SqlDriver
}

