package com.zhuinden.simplestackdemoexamplemvp.util;

import android.view.View;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public abstract class BaseCoordinator<C extends BaseCoordinator<C, P>, P extends BasePresenter<C, P>>
        extends Coordinator {
    public abstract P getPresenter();

    public abstract C getThis();

    Unbinder unbinder;

    View view;

    protected abstract Unbinder bindViews(View view);

    @Override
    protected final void attach(View view) {
        this.unbinder = bindViews(view);
        // noinspection unchecked
        this.view = view;
        attachView(this.view);
        getPresenter().attachCoordinator(getThis());
    }

    @Override
    protected final void detach(View view) {
        getPresenter().detachCoordinator(getThis());
        detachView(this.view);
        unbinder.unbind();
        unbinder = null;
        this.view = null;
    }

    public void attachView(View view) {
    }

    public void detachView(View view) {
    }

    public View getView() {
        return view;
    }

    public <K extends Key> K getKey() {
        // noinspection unchecked
        return (K) Backstack.getKey(view.getContext());
    }
}
