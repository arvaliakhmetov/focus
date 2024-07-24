package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.mvikotlin.core.store.StoreFactory
import details.component.DetailsComponent
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import main.component.IMainComponent
import main.component.MainComponent
import repository.network.WsRepositoryImpl
import root.IRootComponent.Child.*

class RootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory
): IRootComponent, ComponentContext by componentContext {

    private val wsModule = WsRepositoryImpl()

    init {
        doOnResume {
            wsModule.listenSocket()
        }
        doOnDestroy{
            wsModule.shutdown()
        }

    }
    
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, IRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Main, // The initial child component is List
            handleBackButton = true, // Automatically pop from the stack on back button presses
            childFactory = ::child,
        )
    
    private fun child(config: Config, componentContext: ComponentContext): IRootComponent.Child =
        when (config) {
            is Config.Main -> MainChild(mainComponent(componentContext))
            is Config.Details -> DetailsChild(detailsComponent(componentContext, config))
        }
    
    private fun mainComponent(componentContext: ComponentContext): IMainComponent =
        MainComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            onItemSelected = { id: String -> // Supply dependencies and callbacks
                navigation.push(Config.Details(id = id)) // Push the details component
            },
        )

    private fun detailsComponent(componentContext: ComponentContext, config: Config.Details): DetailsComponent =
        DetailsComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            id = config.id, // Supply arguments from the configuration
            onFinished = navigation::pop, // Pop the details component
        )

    override fun onBackClicked(toIndex: Int) {
        navigation.popTo(index = toIndex)
    }


    @Serializable // kotlinx-serialization plugin must be applied
    private sealed interface Config {
        @Serializable
        data object Main: Config

        @Serializable
        data class Details(val id:String) : Config
    }
    
}