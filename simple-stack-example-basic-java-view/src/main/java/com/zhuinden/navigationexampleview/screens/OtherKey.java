package com.zhuinden.navigationexampleview.screens;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigationexampleview.R;
import com.zhuinden.navigationexampleview.core.navigation.BaseKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class OtherKey extends BaseKey {
    public static OtherKey create() {
        return new AutoValue_OtherKey();
    }

    @Override
    public int layout() {
        return R.layout.other_view;
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
