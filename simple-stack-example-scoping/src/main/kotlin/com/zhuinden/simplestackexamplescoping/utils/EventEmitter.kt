package com.zhuinden.simplestackexamplescoping.utils

import com.zhuinden.commandqueue.CommandQueue
import java.util.*

abstract class EventEmitter<E> {
    private val threadId = Thread.currentThread().id

    interface NotificationToken {
        fun stopListening()
    }

    private val commandQueue: CommandQueue<E> = CommandQueue()
    private val notifyObservers: (E) -> Unit = { event ->
        for (i in observers.size - 1 downTo 0) {
            observers[i].invoke(event)
        }
    }

    private val observers: LinkedList<(E) -> Unit> = LinkedList()

    fun startListening(observer: (E) -> Unit): NotificationToken {
        if (threadId != Thread.currentThread().id) {
            throw IllegalStateException("You should register observers only on the thread where the emitter was created")
        }
        val observerCount = observers.size

        observers.add(observer)

        if (observerCount == 0) {
            commandQueue.setReceiver(notifyObservers)
        }

        return object : NotificationToken {
            override fun stopListening() {
                if (threadId != Thread.currentThread().id) {
                    throw IllegalStateException("You should unregister observers only on the thread where the emitter was created")
                }

                @Suppress("NAME_SHADOWING")
                val observerCount = observers.size
                observers.remove(observer)
                if (observerCount == 1) {
                    commandQueue.detachReceiver()
                }
            }
        }
    }

    protected open fun emit(event: E) {
        commandQueue.sendEvent(event)
    }
}