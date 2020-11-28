package com.zhuinden.simplestackdemoexamplemvp.util

import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewKey
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by Zhuinden on 2017.01.27..
 */

class MessageQueue {
    private var messages: MutableMap<ViewKey, Queue<Any>> = ConcurrentHashMap()

    interface Receiver {
        fun receiveMessage(message: Any)
    }

    fun pushMessageTo(recipient: ViewKey, message: Any) {
        var messageQueue: Queue<Any>? = messages[recipient]
        if (messageQueue == null) {
            messageQueue = ConcurrentLinkedQueue()
            messages[recipient] = messageQueue
        }
        messageQueue.add(message)
    }

    fun requestMessages(receiverKey: ViewKey, receiver: Receiver) {
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
