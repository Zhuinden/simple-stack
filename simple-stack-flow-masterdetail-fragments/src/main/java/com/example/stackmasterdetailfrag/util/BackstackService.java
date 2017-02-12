package com.example.stackmasterdetailfrag.util;

import android.content.Context;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;

/**
 * Created by Zhuinden on 2017.02.07..
 */

public class BackstackService {
    private BackstackService() {
    }

    public static final String BACKSTACK_TAG = "simplestack.BACKSTACK";
    public static final String DELEGATE_TAG = "simplestack.BACKSTACK_DELEGATE";

    public static Backstack get(Context context) {
        //noinspection ResourceType
        return (Backstack) context.getSystemService(BACKSTACK_TAG);
    }

    public static BackstackDelegate getDelegate(Context context) {
        // noinspection ResourceType
        return (BackstackDelegate) context.getSystemService(DELEGATE_TAG);
    }
}
