package com.zhuinden.simplestackdemomultistack.application;

import android.content.Context;
import android.os.Parcelable;

import com.zhuinden.simplestack.BackstackDelegate;

/**
 * Created by Owner on 2017. 01. 12..
 */

public interface Key extends Parcelable {
    int layout();

    BackstackDelegate selectDelegate(Context context);
}
