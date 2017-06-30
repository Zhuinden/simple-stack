package com.zhuinden.demo;

import android.content.Context;

import com.zhuinden.simplestack.Backstack;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class BackstackService {
    public static final String TAG = "BackstackService";

    private BackstackService() {
    }

    public static Backstack getBackstack(Context context) {
        //noinspection ResourceType
        return (Backstack)context.getSystemService(TAG);
    }
}
