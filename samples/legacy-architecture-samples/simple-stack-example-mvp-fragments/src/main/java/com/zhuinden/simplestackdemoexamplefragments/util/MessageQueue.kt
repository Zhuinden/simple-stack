package com.zhuinden.simplestackdemoexamplefragments.util

import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentKey
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class MessageQueue {
    val messages: MutableMap<FragmentKey, Queue<Any>> = ConcurrentHashMap()

    interface Receiver {
        fun receiveMessage(message: Any)
    }

    fun pushMessageTo(recipient: FragmentKey, message: Any) {
        var messageQueue: Queue<Any>? = messages[recipient]
        if (messageQueue == null) {
            messageQueue = ConcurrentLinkedQueue()
            messages[recipient] = messageQueue
        }
        messageQueue.add(message)
    }

    fun requestMessages(receiverKey: FragmentKey, receiver: Receiver) {
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
