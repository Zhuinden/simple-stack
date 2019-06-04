package com.zhuinden.simplestackexamplescoping.core.eventemitter

interface EventSource<E> {
    interface NotificationToken {
        fun stopListening()
    }

    fun startListening(observer: (E) -> Unit): NotificationToken
}