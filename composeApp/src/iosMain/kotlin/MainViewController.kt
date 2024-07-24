import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import root.RootComponent

fun MainViewController() = ComposeUIViewController {
    val root = remember {
        RootComponent(
            componentContext = DefaultComponentContext(ApplicationLifecycle()),
            storeFactory = LoggingStoreFactory(TimeTravelStoreFactory())
        )
    }
    App(root)
}



