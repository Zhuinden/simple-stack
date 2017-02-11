package com.zhuinden.stack_rx_example;

import android.content.Context;

import com.zhuinden.simplestack.Backstack;

/**
 * Created by Owner on 2017. 02. 11..
 */

public class BackstackService {
    public static final String TAG = "simplestack.BACKSTACK";

    public static Backstack getBackstack(Context context) {
        // noinspection ResourceType
        return (Backstack)context.getSystemService(TAG);
    }
}
