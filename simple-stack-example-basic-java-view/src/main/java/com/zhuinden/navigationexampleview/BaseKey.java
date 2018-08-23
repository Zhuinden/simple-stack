package com.zhuinden.navigationexampleview;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.simplestack.navigator.StateKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler;

/**
 * Created by Owner on 2017. 06. 29..
 */

public abstract class BaseKey implements StateKey, Parcelable {
    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new FadeViewChangeHandler();
    }
}
