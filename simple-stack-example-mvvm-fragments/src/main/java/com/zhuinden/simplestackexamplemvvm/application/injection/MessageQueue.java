package com.zhuinden.simplestackexamplemvvm.application.injection;

import com.zhuinden.simplestackexamplemvvm.application.BaseKey;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Owner on 2017. 07. 27..
 */

@Singleton
public class MessageQueue {
    @Inject
    public MessageQueue() {
    }

    public interface Receiver {
        void receiveMessage(Object message);
    }

    Map<BaseKey, Queue<Object>> messages = new ConcurrentHashMap<>();

    public void pushMessageTo(BaseKey recipient, Object message) {
        Queue<Object> messageQueue = messages.get(recipient);
        if(messageQueue == null) {
            messageQueue = new ConcurrentLinkedQueue<>();
            messages.put(recipient, messageQueue);
        }
        messageQueue.add(message);
    }

    public void requestMessages(BaseKey receiverBaseKey, Receiver receiver) {
        Queue<Object> messageQueue = messages.get(receiverBaseKey);
        if(messageQueue != null) {
            Iterator<Object> messages = messageQueue.iterator();
            while(messages.hasNext()) {
                receiver.receiveMessage(messages.next());
                messages.remove();
            }
        }
    }
}
