package com.zhuinden.simplestackdemoexamplemvp.util;

import android.view.View;

/**
 * Created by Owner on 2017. 01. 27..
 */

public abstract class BasePresenter<C extends BaseCoordinator<? extends View>> {
    C coordinator;

    public C getCoordinator() {
        return coordinator;
    }

    public final void attachCoordinator(C coordinator) {
        this.coordinator = coordinator;
        onAttach(coordinator);
    }

    public final void detachCoordinator(C coordinator) {
        onDetach(coordinator);
        this.coordinator = null;
    }

    protected abstract void onAttach(C coordinator);

    protected abstract void onDetach(C coordinator);
}
