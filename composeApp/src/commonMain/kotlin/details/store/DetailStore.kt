package details.store

import com.arkivanov.mvikotlin.core.store.Store
import repository.models.Note

interface DetailStore : Store<DetailStore.Intent, DetailStore.State, DetailStore.Lable> {

    sealed class Intent {
        data object AddItem : Intent()
        data class UpdateNote(val note: Note) : Intent()
    }

    data class State(
        val note: Note? = null,
    )
    data class Lable (
        val text:String
    )
}


