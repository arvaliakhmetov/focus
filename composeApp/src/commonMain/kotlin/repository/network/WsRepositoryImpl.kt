package repository.network

import PlatformDispatcher
import getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.database.MainDatabaseRepository
import repository.models.FocusMessageDto
import repository.models.FocusMessageType
import repository.models.Message
import repository.models.NotesForReceive
import repository.models.ReceivedCallback
import repository.models.toJsonString
import repository.models.toNote

class WsRepositoryImpl : WsRepository,KoinComponent {
    override val wsClient: WsClient by inject()
    override val db: MainDatabaseRepository by inject()
    val scope = CoroutineScope(PlatformDispatcher.io)

    init {
        wsClient.onConnectionEstablished = {
            scope.launch {
                val notes = db.getUpdates().first()
                println("NEW_NOTES" + notes.toString())
                if (notes.isNotEmpty()) {
                    wsClient.sendMessage(
                        NotesForReceive(
                            message = FocusMessageType.MESSAGE,
                            clientId = "test_app+${getPlatform().name}",
                            chatId = "262431128",
                            notes = notes.map {
                                FocusMessageDto(
                                    message = FocusMessageType.MESSAGE,
                                    messageId = it.id!!,
                                    clientId = "test_app+${getPlatform().name}",
                                    chatId = "262431128",
                                    received = 0,
                                    canChange = 1,
                                    rawText = it.text,
                                    timestamp = it.timestamp,
                                    stepsIsLoading = false
                                )
                            }
                        ).toJsonString()
                    )

                }
            }

        }
    }

    override fun listenSocket() {
        scope.launch {
            wsClient.receiveMessage { message ->
                scope.launch {
                    val messageReceived = defaultJson.decodeFromString<Message>(message)
                    println(messageReceived.toJsonString())
                    when (messageReceived.message) {
                        FocusMessageType.AUTH -> TODO()
                        FocusMessageType.SYNC -> TODO()
                        FocusMessageType.TIMER -> TODO()
                        FocusMessageType.MESSAGE, FocusMessageType.UPDATES -> {
                            val notes = defaultJson.decodeFromString<NotesForReceive>(message)
                            notes.notes.forEach { note ->
                                when(note.message){
                                    FocusMessageType.DELETE -> {
                                        db.deleteNote(note.messageId)
                                    }
                                    FocusMessageType.MESSAGE -> {
                                        db.addNote(note.toNote())

                                    }
                                    else -> {}
                                }
                                wsClient.sendMessage(
                                    ReceivedCallback(
                                        message = FocusMessageType.SYNC,
                                        clientId = messageReceived.clientId,
                                        chatId = messageReceived.chatId,
                                        messageId = note.messageId
                                    ).toJsonString()
                                )
                            }
                        }

                        FocusMessageType.DELETE -> {
                            val deleteMessage = defaultJson.decodeFromString<ReceivedCallback>(message)
                            db.deleteNote(deleteMessage.messageId)
                        }
                    }
                }
            }

        }
    }

    fun shutdown(){
        scope.cancel()
    }

    override fun sendMessage() {
        TODO("Not yet implemented")
    }
}