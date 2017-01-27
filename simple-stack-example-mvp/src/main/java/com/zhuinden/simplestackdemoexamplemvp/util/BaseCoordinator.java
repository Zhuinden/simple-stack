package com.zhuinden.simplestackdemoexamplemvp.util;

import android.view.View;

import com.squareup.coordinators.Coordinator;

import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public abstract class BaseCoordinator<V extends View>
        extends Coordinator {
    Unbinder unbinder;

    @Override
    protected final void attach(View view) {
        // noinspection unchecked
        this.unbinder = bindViews(view);
        attachView((V) view);
    }

    protected abstract Unbinder bindViews(View view);

    public abstract void attachView(V view);

    @Override
    protected final void detach(View view) {
        detachView((V) view);
        unbinder.unbind();
    }

    public abstract void detachView(V view);
}
