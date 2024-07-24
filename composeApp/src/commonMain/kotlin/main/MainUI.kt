package main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import main.component.IMainComponent
import main.store.MainStore
import theme.FocusTheme


@Composable
fun MainUi(
    component: IMainComponent
) {
    val state by component.store.stateFlow.collectAsState()
    var time by remember{
        mutableStateOf("0")
    }

    FocusTheme {
        MainScreen(
            notes = state.items,
            onTimerStart = {
                component.store.accept(MainStore.Intent.AddTimer)
            },
            onTimerStop = {},
            onNoteClick = { id ->
                component.onNoteClick(id)
            },
            time = state.text,
            onAddNote ={note ->  component.store.accept(MainStore.Intent.AddNote(note))},
            onDelete = {id -> component.store.accept(MainStore.Intent.DeleteNote(id))}
        )
    }


}


