package com.zhuinden.simplestackdemoexamplefragments.util

import com.zhuinden.simplestackdemoexamplefragments.application.Key
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Singleton
class MessageQueue @Inject constructor() {
    val messages: MutableMap<Key, Queue<Any>> = ConcurrentHashMap()

    interface Receiver {
        fun receiveMessage(message: Any)
    }

    fun pushMessageTo(recipient: Key, message: Any) {
        var messageQueue: Queue<Any>? = messages[recipient]
        if (messageQueue == null) {
            messageQueue = ConcurrentLinkedQueue()
            messages[recipient] = messageQueue
        }
        messageQueue.add(message)
    }

    fun requestMessages(receiverKey: Key, receiver: Receiver) {
        val messageQueue = messages[receiverKey]
        if (messageQueue != null) {
            val messages = messageQueue.iterator()
            while (messages.hasNext()) {
                receiver.receiveMessage(messages.next())
                messages.remove()
            }
        }
    }
}
