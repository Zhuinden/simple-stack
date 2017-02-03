package com.zhuinden.simplestackdemoexamplefragments.util;

import com.zhuinden.simplestack.Bundleable;

/**
 * Created by Owner on 2017. 01. 27..
 */

public abstract class BasePresenter<F extends BaseFragment<F, P>, P extends BasePresenter<F, P>>
        implements Bundleable {
    F fragment;

    public F getFragment() {
        return fragment;
    }

    public final void attachFragment(F fragment) {
        this.fragment = fragment;
        onAttach(fragment);
    }

    public final void detachFragment(F fragment) {
        onDetach(fragment);
        this.fragment = null;
    }

    protected abstract void onAttach(F fragment);

    protected abstract void onDetach(F fragment);
}
