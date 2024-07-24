package main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import getPlatform
import kotlinx.coroutines.delay
import repository.models.Note
import utils.rememberTopSafeArea

enum class DragValue { Start, Center, End }

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    notes: List<Note>,
    onTimerStart: () -> Unit,
    onTimerStop: () -> Unit,
    onAddNote: (note: Note) -> Unit,
    time: String,
    onNoteClick: (id: String) -> Unit,
    onDelete: (id: String) -> Unit
) {
    var offsetX by remember { mutableStateOf(0.dp) }
    val animatedOffset by animateDpAsState(offsetX)
    val density = LocalDensity.current
    println(offsetX)
    val platformTrashold = remember {
        if (getPlatform().name.contains("1")) {
            50
        } else {
            25
        }
    }
    var editMode by remember { mutableStateOf(false) }
    var canEdit by remember { mutableStateOf(false) }
    val topSafePadding = rememberTopSafeArea()
    val newNotes by rememberUpdatedState(notes)
    var landScape by remember { mutableStateOf(false) }
    val width = if (!landScape) 0.dp else 200.dp
    LaunchedEffect(landScape) {
        if (landScape) {
            offsetX = 0.dp
        }
    }
    LaunchedEffect(editMode){
        if(editMode){
            canEdit = editMode
        }else{
            delay(1000)
            canEdit = editMode
        }


    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .onSizeChanged {
                landScape = it.height < it.width
            }
    ) {
        val height = maxHeight
        Scaffold(
            modifier = Modifier
                .offset(animatedOffset)
                .width(maxWidth - width)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (!landScape) {
                            if(canEdit){
                                if (delta < -platformTrashold) editMode = false
                                if (delta > platformTrashold) editMode = false
                                println(delta.toString()+"editmode")
                            }else{
                                if (delta < -platformTrashold) offsetX = (-200).dp
                                if (delta > platformTrashold) offsetX = 0.dp
                                println(delta.toString()+"no_editmode")
                            }

                            if (delta > platformTrashold && animatedOffset == 0.dp) editMode = true
                        }
                    }
                )
                .align(Alignment.TopStart),
            contentWindowInsets = WindowInsets.ime
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
                    .height(maxHeight / 2),
                contentAlignment = Alignment.CenterEnd
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "infinite")
                val color by infiniteTransition.animateColor(
                    initialValue = Color.Green,
                    targetValue = Color.Blue,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "color"
                )
                Text(
                    modifier = Modifier.padding(top = topSafePadding, end = 28.dp)
                        .offset(-animatedOffset),
                    text = "FOCUS_APP",
                    fontWeight = FontWeight(700),
                    color = color
                )
            }
            val offsetTarget by remember(editMode){
                derivedStateOf {
                    if(editMode) 20.dp else 0.dp
                }
            }
            val offset by animateDpAsState(offsetTarget)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                            .height(maxHeight / 2),
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
                tasksList(
                    onNoteClick = {
                        onNoteClick(it)
                    },
                    editMode = editMode,
                    onDelete = onDelete,
                    notes = newNotes,
                    offset = offset,
                )
                item {
                    Spacer(modifier = Modifier.height(maxHeight/2))
                }
            }

        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {

            LazyColumn(
                modifier = Modifier.width(200.dp)
                    .weight(1f)
                    .offset(200.dp + if (!landScape) animatedOffset else (-200).dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(modifier = Modifier.height(height / 2).width(200.dp))
                }
                repeat(20) {
                    item {
                        TextButton(
                            onClick = {}
                        ) {
                            Text("$it menu item")
                        }

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }


        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.tasksList(
    notes: List<Note>,
    editMode: Boolean,
    offset: Dp,
    onNoteClick: (id: String) -> Unit,
    onDelete: (id: String) -> Unit
) {
    items(
        items = notes,
        key = { it.id!! }
    ) { note ->
        Box(
            modifier = Modifier.animateItemPlacement()
                .padding(horizontal = 28.dp)
        ) {
            AnimatedVisibility(
                visible = editMode,
                enter = slideInHorizontally { -it }+ fadeIn(),
                exit = slideOutHorizontally { -it }+ fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).offset((-5).dp)
                        .clickable {
                            onDelete(note.id!!)
                        }
                )
            }
            Card(
                modifier = Modifier
                    .offset(offset)
                    .clickable(
                        enabled = !editMode,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onNoteClick(note.id!!) }
                    )
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = note.text,
                )
            }
        }

    }
}