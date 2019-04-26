package com.chibatching.flowreactiveuisample

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.flow.flowViaChannel

fun <T> LiveData<T>.asFlow() = flowViaChannel<T?> {
    it.offer(value)
    val observer = Observer<T> { t -> it.offer(t) }
    observeForever(observer)
    it.invokeOnClose {
        removeObserver(observer)
    }
}
