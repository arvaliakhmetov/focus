package details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.material.icons.twotone.ChatBubbleOutline
import androidx.compose.material.icons.twotone.FormatListNumbered
import androidx.compose.material.icons.twotone.Workspaces
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import details.component.DetailsComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import repository.models.Note
import repository.models.Step

@OptIn(
    ExperimentalCoroutinesApi::class, ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun DetailsUi(
    component: DetailsComponent
) {
    val note by component.note.subscribeAsState()
    var showSheet by remember { mutableStateOf(false) }
    val state by component.store.stateFlow.collectAsState()
    val density = LocalDensity.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ) {
        true
    }
    var expandAdditional by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardHidden = WindowInsets.ime.getBottom(density) == 0
    var rotation by remember { mutableStateOf(0F) }
    val animatedRotation by animateFloatAsState(rotation)
    val labels by component.store.labels.collectAsState(null)
    LaunchedEffect(isKeyboardHidden) {
        rotation = if (isKeyboardHidden) {
            0F
        } else {
            -90F
        }
    }

    LaunchedEffect(state.note) {
        component.updatedNote(state.note ?: Note())
    }
    LaunchedEffect(labels) {
        labels?.let {
            when (it.text) {
                "GO_BACK" -> component.onFinished {

                }
            }
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(WindowInsets.ime.asPaddingValues()),
            contentAlignment = Alignment.BottomEnd
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()

            ) {
                item {
                    Box(modifier = Modifier.height(maxHeight / 2))
                }
                item {
                    IconButton(
                        onClick = {
                            expandAdditional = !expandAdditional
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, null)
                    }
                }
                item {

                }
                if (expandAdditional) {
                    item {

                    }

                }
                item {
                    FocusTextField("text", note.text) {
                        component.updatedNote(note.copy(text = it))
                    }
                }

                item {
                    Box(modifier = Modifier.height(maxHeight / 2))
                }

            }
            LazyVerticalGrid(
                modifier = Modifier.padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
                columns = GridCells.Fixed(3),
            ) {
                item {
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ElevatedSuggestionChip(
                            onClick = {
                                showSheet = true
                            },
                            icon = {
                                Icon(Icons.Default.ControlCamera, null)
                            },
                            label = {
                                Text("ask AI")
                            }
                        )
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        FloatingActionButton(
                            shape = CircleShape,
                            onClick = {
                                if (!isKeyboardHidden) {
                                    keyboardController?.hide()
                                } else {
                                    component.onBackPress()
                                }

                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "back",
                                modifier = Modifier.rotate(animatedRotation)
                            )
                        }
                    }
                }
            }

        }

    }
    AnimatedVisibility(
        visible = showSheet
    ) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
            }
        ) {
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedSuggestionChip(
                    icon = {
                        Icon(Icons.TwoTone.Workspaces, contentDescription = null)
                    },
                    onClick = {},
                    label = { Text("analyze") }
                )
                ElevatedSuggestionChip(
                    icon = {
                        Icon(Icons.TwoTone.FormatListNumbered, contentDescription = null)
                    },
                    onClick = {},
                    label = { Text("generate steps") }
                )
                ElevatedSuggestionChip(
                    icon = {
                        Icon(Icons.TwoTone.ChatBubbleOutline, contentDescription = null)
                    },
                    onClick = {},
                    label = { Text("chat") }
                )
            }
        }
    }

}

@Composable
fun FocusTextField(title: String, text: String?, onValueChange: (String) -> Unit) {
    TextField(
        label = {
            Text(title)
        },
        value = text ?: "",
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent

        ),
        onValueChange = onValueChange
    )
}

@Composable
fun FocusStepField(
    steps: Step,
    onCheckChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row {

        TextField(
            modifier = Modifier.weight(1f),
            placeholder = {
                Text("New step")
            },
            leadingIcon = {
                Checkbox(
                    checked = steps.isChecked == 1L,
                    onCheckedChange = onCheckChange
                )
            },
            trailingIcon = {
                AnimatedVisibility(steps.text.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                            .clickable {
                                onDelete()
                            }
                    )
                }
            },
            value = steps.text,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent

            ),
            onValueChange = onValueChange
        )
    }

}