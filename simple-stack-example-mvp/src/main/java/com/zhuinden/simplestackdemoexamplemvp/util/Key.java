package com.zhuinden.simplestackdemoexamplemvp.util;

import android.os.Parcelable;

import com.squareup.coordinators.Coordinator;

/**
 * Created by Owner on 2017. 01. 12..
 */

public interface Key extends Parcelable {
    int layout();

    Coordinator newCoordinator();
}
