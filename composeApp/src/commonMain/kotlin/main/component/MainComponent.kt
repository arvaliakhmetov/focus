package main.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import main.store.MainStore
import main.store.mainStore

@OptIn(ExperimentalCoroutinesApi::class)
class MainComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val onItemSelected: (id: String) -> Unit
) : IMainComponent, ComponentContext by componentContext{

    override val store = instanceKeeper.getStore { storeFactory.mainStore() }

    override fun onNoteClick(id: String) = onItemSelected(id)

    init {
        componentContext.lifecycle.doOnResume {
            store.accept(MainStore.Intent.Connect)
        }
    }

}