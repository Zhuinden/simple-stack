package com.zhuinden.simplestackdemomultistack.util;

import android.content.Context;

import com.zhuinden.simplestack.Backstack;

/**
 * Created by Owner on 2017. 01. 31..
 */

public class BackstackService {
    public static final String TAG = "BackstackService";

    public static Backstack get(Context context) {
        // noinspection ResourceType
        return (Backstack) context.getSystemService(TAG);
    }
}
