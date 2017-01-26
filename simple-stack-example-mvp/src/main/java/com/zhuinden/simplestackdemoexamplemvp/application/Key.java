package com.zhuinden.simplestackdemoexamplemvp.application;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent;

/**
 * Created by Owner on 2017. 01. 12..
 */

public interface Key extends Parcelable {
    int layout();

    Coordinator newCoordinator(SingletonComponent singletonComponent);

    int menu();

    int navigationViewId();

    boolean isFabVisible();

    boolean shouldShowUp();

    View.OnClickListener fabClickListener(View view);

    @DrawableRes
    int fabDrawableIcon();
}
