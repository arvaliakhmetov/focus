package repository.network

import getPlatform
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import repository.models.FocusMessageDto
import repository.models.FocusMessageType
import repository.models.Message
import repository.models.NotesForReceive
import repository.models.toJsonString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.database.MainDatabaseRepository

val defaultJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

class WsClient : KoinComponent {
    private val client = getHttpClient()
    private var session: DefaultClientWebSocketSession? = null
    lateinit var onConnectionEstablished: () -> Unit

    init {
        runBlocking {
            try {
                establishConnection()
            } catch (_: Throwable) {
            }

        }
    }

    private suspend fun establishConnection() {
        session = client.webSocketSession(
            method = HttpMethod.Get,
            host = "192.168.1.100",
            port = 8080,
            path = "/ws"
        )
        sendMessage(
            Message(
                message = FocusMessageType.AUTH,
                clientId = "test_app+${getPlatform().name}",
                chatId = "262431128"
            ).toJsonString()
        )
        sendMessage(
            Message(
                message = FocusMessageType.UPDATES,
                clientId = "test_app+${getPlatform().name}",
                chatId = "262431128"
            ).toJsonString()
        )
        onConnectionEstablished()
    }

    private suspend fun ensureSession() {
        if (session == null || session?.isActive == false) {
            try {
                establishConnection()
            } catch (e: Exception) {
                e.printStackTrace()
                delay(1000) // Задержка перед повторной попыткой подключения
            }
        }
    }

    suspend fun sendMessage(message: String) {
        try {
            ensureSession()
            session?.outgoing?.send(Frame.Text(text = message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun receiveMessage(message: (String) -> Unit) {
        while (true) {
            try {
                ensureSession()
                session?.incoming?.consumeEach {
                    when (it) {
                        is Frame.Text -> {
                            message.invoke(it.readText())
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                session = null // Обнуляем сессию, чтобы её восстановить
                delay(5000) // Задержка перед повторной попыткой подключения
            }
        }
    }
}