package com.zhuinden.simplestackdemomultistack.application;

import android.content.Context;
import android.os.Parcelable;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestackdemomultistack.util.ServiceLocator;

/**
 * Created by Owner on 2017. 01. 12..
 */

public abstract class Key
        implements Parcelable {
    public abstract int layout();

    public final BackstackDelegate selectDelegate(Context context) {
        return ServiceLocator.getService(context, stackIdentifier());
    }

    public abstract String stackIdentifier();
}
