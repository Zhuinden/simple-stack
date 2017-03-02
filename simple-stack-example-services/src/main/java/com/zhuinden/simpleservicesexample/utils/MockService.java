package com.zhuinden.simpleservicesexample.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;

/**
 * Created by Zhuinden on 2017.03.02..
 */

public class MockService
        implements Bundleable {
    private static final String TAG = "MockService";

    private String name;

    public MockService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MockService[" + name + "]";
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        Log.i(TAG, "To Bundle for [" + name + "]");
        return new StateBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        Log.i(TAG, "From Bundle for [" + name + "] with [" + bundle + "]");
        if(bundle != null) {

        }
    }
}
