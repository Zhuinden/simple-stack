package com.zhuinden.simplestackdemoexamplemvp.application;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.squareup.coordinators.Coordinator;

/**
 * Created by Owner on 2017. 01. 12..
 */

public interface Key extends Parcelable {
    int layout();

    Coordinator newCoordinator();

    int menu();

    int navigationViewId();

    boolean isFabVisible();

    boolean shouldShowUp();

    View.OnClickListener fabClickListener();

    @DrawableRes
    int fabDrawableIcon();
}
