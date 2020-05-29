package com.zhuinden.navigationexampleview.core.navigation;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.simplestack.navigator.DefaultViewKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler;

/**
 * Created by Owner on 2017. 06. 29..
 */

public abstract class BaseKey implements DefaultViewKey, Parcelable {
    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new FadeViewChangeHandler();
    }
}
