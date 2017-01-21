package com.zhuinden.simplestack;

/**
 * Created by Owner on 2017. 01. 12..
 */
public interface StateChanger {
    interface Callback {
        void stateChangeComplete();
    }

    void handleStateChange(StateChange stateChange, Callback completionCallback);
}
