package com.zhuinden.simplestackdemoexamplemvp.util;

import android.view.View;

/**
 * Created by Owner on 2017. 01. 27..
 */

public abstract class BasePresenter<V extends View, P extends BasePresenter<V, P>> {
    V view;

    public V getView() {
        return view;
    }

    public final void attachView(V view) {
        this.view = view;
        onAttach(view);
    }

    public final void detachView(V view) {
        onDetach(view);
        this.view = null;
    }

    protected abstract void onAttach(V view);

    protected abstract void onDetach(V view);
}
