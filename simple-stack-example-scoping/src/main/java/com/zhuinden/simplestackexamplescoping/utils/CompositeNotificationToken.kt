package com.zhuinden.simplestackexamplescoping.utils

import java.util.*

class CompositeNotificationToken : EventEmitter.NotificationToken {
    private val threadId = Thread.currentThread().id

    private val notificationTokens: LinkedList<EventEmitter.NotificationToken> = LinkedList()

    fun add(notificationToken: EventEmitter.NotificationToken) {
        notificationTokens.add(notificationToken)
    }

    private var isDisposing = false

    override fun stopListening() {
        if (threadId != Thread.currentThread().id) {
            throw IllegalStateException("Cannot stopListening notification token on a different thread where it was created")
        }
        if (isDisposing) {
            return
        }
        isDisposing = true
        val size = notificationTokens.size
        for (i in size - 1 downTo 0) {
            val token = notificationTokens.removeAt(i)
            token.stopListening()
        }
        isDisposing = false
    }

    operator fun plusAssign(notificationToken: EventEmitter.NotificationToken) {
        add(notificationToken)
    }
}