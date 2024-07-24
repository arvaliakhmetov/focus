package main.store

import PlatformDispatcher
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import getPlatform
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import main.store.MainStore.*
import repository.database.MainDatabaseRepository
import repository.network.WsClient
import repository.network.defaultJson
import repository.models.FocusMessageDto
import repository.models.FocusMessageType
import repository.models.Message
import repository.models.Note
import repository.models.NotesForReceive
import repository.models.ReceivedCallback
import repository.models.toJsonString
import repository.models.toNote
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.mainStore(): MainStore =
    object : MainStore, Store<Intent, State, Nothing> by create(
        name = "ListStore",
        initialState = State(),
        bootstrapper = SimpleBootstrapper(Action.Init),
        executorFactory = {
            ExecutorImpl(
                mainContext = PlatformDispatcher.main,
                ioContext = PlatformDispatcher.io,
            )
        },
        reducer = { reduce(it) },
    ) {}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed interface Action : JvmSerializable {
    data object Init : Action
    data class AddNote(val note: Note) : Action
    data class DeleteNote(val id: String) : Action
    data class SaveItem(val id: String) : Action
}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed interface Msg : JvmSerializable {
    data class Timer(val timeRemaining: Long) : Msg
    data class Loaded(val items: List<Note>) : Msg
    data class ChangeText(val text: String) : Msg
}

private class ExecutorImpl(
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
) : CoroutineExecutor<Intent, Action, State, Msg, Nothing>(mainContext), KoinComponent {
    val db: MainDatabaseRepository by inject()
    val ws: WsClient by inject()

    override fun executeAction(action: Action) {
        when (action) {
            is Action.Init -> init()
            is Action.SaveItem -> dispatch(Msg.ChangeText(action.id))
            is Action.AddNote -> addNote(action.note)
            is Action.DeleteNote -> deleteNote(action.id)
        }
    }

    private fun init() {
        scope.launch {
            db.getAllNotes().collect {
                val notes = it.map { it.toNote() }.sortedBy { note -> note.timestamp }.reversed()
                dispatch(Msg.Loaded(notes))
            }
        }
    }

    private fun sync() {
        scope.launch(ioContext) {
            val notes = db.getUpdates().firstOrNull()
            println("NEW_NOTES" + notes.toString())
            if (notes?.isNotEmpty() == true) {
                ws.sendMessage(
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

    private fun deleteNote(id: String) {
        scope.launch(ioContext) {
            withContext(ioContext) {
                ws.sendMessage(
                    ReceivedCallback(
                        clientId = "test_app+${getPlatform().name}",
                        chatId = "262431128",
                        message = FocusMessageType.DELETE,
                        messageId = id
                    ).toJsonString()
                )
            }
        }
    }

    private fun addNote(note: Note) {
        scope.launch {
            withContext(ioContext) {
                db.addNote(note)
            }
        }
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.AddTimer -> {
                scope.launch {
                    withContext(ioContext) {
                        db.setTime(1000L)
                    }
                }
            }

            Intent.ChangeText -> executeAction(Action.SaveItem("NewText"))
            is Intent.AddNote -> executeAction(action = Action.AddNote(intent.note))
            is Intent.DeleteNote -> executeAction(action = Action.DeleteNote(intent.id))
            Intent.Connect -> {
                sync()
            }
        }
    }

    private fun toggleDone(id: String) {
        forward(Action.SaveItem(id = id))
    }
}

private fun State.reduce(msg: Msg): State =
    when (msg) {
        is Msg.Loaded -> copy(items = msg.items)
        is Msg.ChangeText -> copy(text = msg.text)
        is Msg.Timer -> copy(text = msg.timeRemaining.toString())
        else -> this
    }