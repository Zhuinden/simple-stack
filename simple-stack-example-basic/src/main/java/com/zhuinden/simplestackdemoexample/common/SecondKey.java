package com.zhuinden.simplestackdemoexample.common;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.NoOpViewChangeHandler;
import com.zhuinden.simplestackdemoexample.R;
/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue // parcelable, hashcode, equals, toString
public abstract class SecondKey
        implements Key {
    public static SecondKey create() {
        return new AutoValue_SecondKey();
    }

    @Override
    public int layout() {
        return R.layout.path_second;
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new NoOpViewChangeHandler();
    }
}
