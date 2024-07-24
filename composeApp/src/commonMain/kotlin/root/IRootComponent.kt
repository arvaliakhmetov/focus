package root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import details.component.DetailsComponent
import details.component.IDetailsComponent
import main.component.IMainComponent

interface IRootComponent {
    val stack: Value<ChildStack<*, Child>>

    // It's possible to pop multiple screens at a time on iOS
    fun onBackClicked(toIndex: Int)

    // Defines all possible child components
    sealed class Child {
        class MainChild(val component: IMainComponent) : Child()
        class DetailsChild(val component: DetailsComponent) : Child()
    }
}