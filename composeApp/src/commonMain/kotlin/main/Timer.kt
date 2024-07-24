package main

import PlatformDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Timer(val updateInterface: (Int) -> Unit) {
    private var timer: Job? = null

    fun start(seconds: Int) {
        val callback = updateInterface
        timer = CoroutineScope(PlatformDispatcher.io).launch {
            repeat(seconds) {
                callback(it)
                delay(1000)
            }
            callback(seconds)
        }
    }

    fun stop() {
        timer?.cancel()
    }
}