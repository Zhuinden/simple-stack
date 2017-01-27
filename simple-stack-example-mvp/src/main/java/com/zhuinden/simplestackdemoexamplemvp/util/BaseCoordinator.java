package com.zhuinden.simplestackdemoexamplemvp.util;

import android.view.View;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public abstract class BaseCoordinator<V extends View>
        extends Coordinator {
    Unbinder unbinder;

    V view;

    @Override
    protected final void attach(View view) {
        this.unbinder = bindViews(view);
        // noinspection unchecked
        this.view = (V) view;
        attachView(this.view);
    }

    protected abstract Unbinder bindViews(View view);

    public abstract void attachView(V view);

    @Override
    protected final void detach(View view) {
        detachView(this.view);
        unbinder.unbind();
        unbinder = null;
        this.view = null;
    }

    public abstract void detachView(V view);

    public V getView() {
        return view;
    }

    public <K extends Key> K getKey() {
        // noinspection unchecked
        return (K) Backstack.getKey(view.getContext());
    }
}
