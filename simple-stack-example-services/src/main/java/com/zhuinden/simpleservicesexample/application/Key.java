package com.zhuinden.simpleservicesexample.application;

import android.os.Parcelable;

import com.zhuinden.simplestack.Services;


/**
 * Created by Zhuinden on 2017.02.14..
 */

public abstract class Key
        implements Parcelable {
    public abstract int layout();

    public abstract void bindServices(Services.Builder builder);
}
