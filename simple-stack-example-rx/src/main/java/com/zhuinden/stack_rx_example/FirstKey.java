package com.zhuinden.stack_rx_example;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 02. 11..
 */

@AutoValue
public abstract class FirstKey extends Key {
    @Override
    public int layout() {
        return R.layout.path_first;
    }

    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }
}
