package main.component

import com.arkivanov.mvikotlin.core.store.Store
import main.store.MainStore

interface IMainComponent {
    val store:Store<MainStore.Intent, MainStore.State, Nothing>
    fun onNoteClick(id: String)
}