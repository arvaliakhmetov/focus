package details.store

import PlatformDispatcher
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import details.store.DetailStore.Intent
import details.store.DetailStore.State
import getPlatform
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.database.MainDatabaseRepository
import repository.models.FocusMessageDto
import repository.models.FocusMessageType
import repository.models.Note
import repository.models.NotesForReceive
import repository.models.toJsonString
import repository.network.WsClient
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.detailStore(id: String): DetailStore =
    object : DetailStore, Store<Intent, State, DetailStore.Lable> by create(
        name = "ListStore",
        initialState = State(),
        bootstrapper = SimpleBootstrapper(Action.Init(id)),
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
    data class Init(val id: String) : Action
    data class UpdateNote(val note: Note) : Action
}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed interface Msg : JvmSerializable {
    data class Loaded(val note: Note) : Msg
}

private class ExecutorImpl(
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
) : CoroutineExecutor<Intent, Action, State, Msg, DetailStore.Lable>(mainContext), KoinComponent{
    val db: MainDatabaseRepository by inject()
    val ws: WsClient by inject()
    override fun executeAction(action: Action) {
        when (action) {
            is Action.Init -> init(action.id)
            is Action.UpdateNote -> {}
            else -> {}
        }
    }

    private fun init(id: String) {
        scope.launch {
            db.getNote(id).collect{
                dispatch(Msg.Loaded(it))
            }

        }
    }
    private fun sync(note:Note){
        scope.launch(mainContext) {
            ws.sendMessage(
                NotesForReceive(
                    message = FocusMessageType.MESSAGE,
                    clientId = "test_app+${getPlatform().name}",
                    chatId = "262431128",
                    notes = listOf(
                        FocusMessageDto(
                            message = FocusMessageType.MESSAGE,
                            messageId = note.id!!,
                            clientId = "test_app+${getPlatform().name}",
                            chatId = "262431128",
                            received = 0,
                            canChange = 1,
                            rawText = note.text,
                            timestamp = note.timestamp,
                            stepsIsLoading = false
                        )
                    )
                ).toJsonString()
            )
        }.invokeOnCompletion {
            this.publish(DetailStore.Lable("GO_BACK"))
        }
    }


    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.AddItem -> {}
            is Intent.UpdateNote -> updateNote(intent.note)
        }
    }

    private fun updateNote(note: Note) {
        scope.launch(mainContext) {
            db.addNote(note)
        }.invokeOnCompletion {
            sync(note)
        }

    }
}

private fun State.reduce(msg: Msg): State =
    when (msg) {
        is Msg.Loaded -> copy(note = msg.note)
        else -> this
    }