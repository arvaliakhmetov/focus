package details.component

import com.arkivanov.mvikotlin.core.store.Store
import details.store.DetailStore
import main.model.Item
import main.store.MainStore

interface IDetailsComponent {
    val store: Store<DetailStore.Intent, DetailStore.State, DetailStore.Lable>
    fun onBackPress()
}