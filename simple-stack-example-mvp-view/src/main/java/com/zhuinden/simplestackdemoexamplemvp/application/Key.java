package com.zhuinden.simplestackdemoexamplemvp.application;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.zhuinden.simplestack.navigator.StateKey;

/**
 * Created by Owner on 2017. 01. 12..
 */

public interface Key extends StateKey, Parcelable {
    int layout();

    int menu();

    int navigationViewId();

    boolean isFabVisible();

    boolean shouldShowUp();

    View.OnClickListener fabClickListener(View view);

    @DrawableRes
    int fabDrawableIcon();
}
