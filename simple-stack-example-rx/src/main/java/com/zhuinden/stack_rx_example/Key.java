package com.zhuinden.stack_rx_example;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.simplestack.navigator.StateKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.NoOpViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;

/**
 * Created by Owner on 2017. 02. 11..
 */

public abstract class Key implements StateKey, Parcelable {
    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new NoOpViewChangeHandler();
    }
}
