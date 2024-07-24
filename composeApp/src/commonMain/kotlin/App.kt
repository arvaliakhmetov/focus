import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import details.DetailsUi
import main.MainUi
import org.jetbrains.compose.ui.tooling.preview.Preview
import root.IRootComponent.Child
import root.RootComponent
import theme.FocusTheme

@Composable
@Preview
fun App(
    rootComponent: RootComponent
) {
    FocusTheme {
        Children(
            animation = stackAnimation(animator = slide()),
            stack = rootComponent.stack
        ){
            when(val child = it.instance){
                is Child.DetailsChild -> DetailsUi(child.component)
                is Child.MainChild -> MainUi(child.component)
            }
        }
    }
}