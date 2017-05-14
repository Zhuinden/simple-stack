package com.example.mortar.util;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public abstract class BaseKey implements Key {
    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().isAssignableFrom(getClass());
    }
}
