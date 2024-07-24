package utils

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

data class DispatcherHelper(
    val main: CoroutineContext,
    val io: CoroutineContext
)
