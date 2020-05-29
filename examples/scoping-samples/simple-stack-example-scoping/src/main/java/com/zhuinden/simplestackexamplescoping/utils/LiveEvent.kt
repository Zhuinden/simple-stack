package com.zhuinden.simplestackexamplescoping.utils


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.zhuinden.eventemitter.EventSource

private class LiveEvent<T> constructor(
    private val eventSource: EventSource<T>,
    private val lifecycleOwner: LifecycleOwner,
    private val observer: EventSource.EventObserver<T>
) : LifecycleObserver {
    private val threadId = Thread.currentThread().id

    init {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            lifecycleOwner.lifecycle.addObserver(this)
        }
    }

    private var isActive: Boolean = false
    private var notificationToken: EventSource.NotificationToken? = null

    private fun shouldBeActive(): Boolean {
        return lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    private fun disposeObserver() {
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            stopListening()
            disposeObserver()
            return
        }
        checkIfActiveStateChanged(shouldBeActive())
    }

    private fun checkIfActiveStateChanged(newActive: Boolean) {
        if (newActive == isActive) {
            return
        }
        val wasActive = isActive
        isActive = newActive
        val isActive = isActive

        if (!wasActive && isActive) {
            stopListening()
            notificationToken = eventSource.startListening(observer)
        }

        if (wasActive && !isActive) {
            stopListening()
        }
    }

    private fun stopListening() {
        notificationToken?.stopListening()
        notificationToken = null
    }
}

fun <T> EventSource<T>.observe(lifecycleOwner: LifecycleOwner, eventObserver: (T) -> Unit) {
    LiveEvent(this, lifecycleOwner, EventSource.EventObserver<T> { event -> eventObserver.invoke(event) })
}