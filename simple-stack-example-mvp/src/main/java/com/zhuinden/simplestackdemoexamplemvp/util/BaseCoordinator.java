package com.zhuinden.simplestackdemoexamplemvp.util;

import android.view.View;

import com.squareup.coordinators.Coordinator;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public abstract class BaseCoordinator<V extends View>
        extends Coordinator {
    @Override
    protected final void attach(View view) {
        // noinspection unchecked
        attachView((V) view);
    }

    public abstract void attachView(V view);

    @Override
    protected final void detach(View view) {
        // noinspection unchecked
        detachView((V) view);
    }

    public abstract void detachView(V view);
}
