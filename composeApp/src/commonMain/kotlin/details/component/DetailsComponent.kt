package details.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import details.store.DetailStore
import details.store.detailStore
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import repository.models.Note
import repository.models.Status
import kotlin.reflect.KFunction1

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsComponent(
    val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    id: String,
    val onFinished: KFunction1<(isSuccess: Boolean) -> Unit, Unit>,
) : IDetailsComponent, ComponentContext by componentContext {

    override val store = instanceKeeper.getStore { storeFactory.detailStore(id) }
    val note = MutableValue(Note())
    override fun onBackPress() {
        if (note.value.toString() != store.state.note.toString()) {
            store.accept(
                DetailStore.Intent.UpdateNote(
                    note.value.copy(
                        timestamp = getTimeMillis(),
                        status = Status.NEW.name
                    )
                )
            )
        }else{
            onFinished{}
        }
    }

    init {
        lifecycle.doOnPause {
            if (note.value.toString() != store.state.note.toString()) store.accept(
                DetailStore.Intent.UpdateNote(
                    note.value.copy(
                        timestamp = getTimeMillis(),
                        status = Status.NEW.name
                    )
                )
            )
        }
    }

    fun updatedNote(_note: Note) {
        note.update { _note }
    }


}