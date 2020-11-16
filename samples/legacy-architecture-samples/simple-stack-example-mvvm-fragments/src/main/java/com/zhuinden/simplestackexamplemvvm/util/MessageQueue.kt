package com.zhuinden.simplestackexamplemvvm.util


import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Owner on 2017. 07. 27..
 */
@Singleton
class MessageQueue @Inject constructor() {
    fun interface Receiver {
        fun receiveMessage(message: Any)
    }

    val messages: MutableMap<BaseKey, Queue<Any>> = ConcurrentHashMap()

    fun pushMessageTo(recipient: BaseKey, message: Any) {
        var messageQueue = messages[recipient]
        if (messageQueue == null) {
            messageQueue = ConcurrentLinkedQueue()
            messages[recipient] = messageQueue
        }
        messageQueue.add(message)
    }

    fun requestMessages(receiverBaseKey: BaseKey, receiver: Receiver) {
        val messageQueue = messages[receiverBaseKey]
        if (messageQueue != null) {
            val messages = messageQueue.iterator()
            while (messages.hasNext()) {
                receiver.receiveMessage(messages.next())
                messages.remove()
            }
        }
    }
}