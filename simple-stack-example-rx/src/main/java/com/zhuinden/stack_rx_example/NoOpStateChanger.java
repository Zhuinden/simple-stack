package com.zhuinden.stack_rx_example;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Owner on 2017. 02. 11..
 */

public class NoOpStateChanger implements StateChanger {
    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        completionCallback.stateChangeComplete();
    }
}
