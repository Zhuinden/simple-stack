package com.zhuinden.simplestack;

/**
 * The interface that is responsible for handling when the state in the backstack changes.
 *
 * Created by Zhuinden on 2017. 01. 12..
 */
public interface StateChanger {
    interface Callback {
        void stateChangeComplete();
    }

    void handleStateChange(StateChange stateChange, Callback completionCallback);
}
