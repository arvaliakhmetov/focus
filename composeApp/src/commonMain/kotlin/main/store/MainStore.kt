package main.store

import com.arkivanov.mvikotlin.core.store.Store
import repository.models.Note

interface MainStore : Store<MainStore.Intent, MainStore.State, Nothing> {

    sealed class Intent {
        data object AddTimer : Intent()
        data object ChangeText : Intent()
        data object Connect: Intent()
        data class AddNote(val note: Note) : Intent()
        data class DeleteNote(val id: String) : Intent()
    }

    data class State(
        val items: List<Note> = emptyList(),
        val text: String = ""
    )
}