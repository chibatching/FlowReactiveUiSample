package com.chibatching.flowreactiveuisample

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowViaChannel

fun <T> LiveData<T>.asFlow() = flowViaChannel<T?> {
    it.send(value)
    val observer = Observer<T> { t -> it.offer(t) }
    observeForever(observer)
    it.invokeOnClose {
        removeObserver(observer)
    }
}


fun <T> Flow<T>.debounce(waitMillis: Long) = flow {
    coroutineScope {
        val context = coroutineContext
        var delayPost: Deferred<Unit>? = null
        collect {
            delayPost?.cancel()
            delayPost = async(Dispatchers.Default) {
                delay(waitMillis)
                withContext(context) {
                    emit(it)
                }
            }
        }
    }
}
