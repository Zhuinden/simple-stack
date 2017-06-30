package com.zhuinden.simplestackdemoexamplefragments.application;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Owner on 2017. 01. 12..
 */

public interface Key
        extends Parcelable {
    int layout();

    Fragment newFragment();

    String getFragmentTag();

    int menu();

    int navigationViewId();

    boolean isFabVisible();

    boolean shouldShowUp();

    View.OnClickListener fabClickListener(Fragment fragment);

    @DrawableRes
    int fabDrawableIcon();
}
