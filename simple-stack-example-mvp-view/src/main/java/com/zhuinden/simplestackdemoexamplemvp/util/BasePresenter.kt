package com.zhuinden.simplestackdemoexamplemvp.util

import android.view.View

/**
 * Created by Owner on 2017. 01. 27..
 */

abstract class BasePresenter<V : View>: MvpPresenter<V> {
    var view: V? = null
        private set

    final override fun attachView(view: V) {
        this.view = view
        onAttach(view)
    }

    final override fun detachView(view: V) {
        onDetach(view)
        this.view = null
    }

    protected open fun onAttach(view: V) {}

    protected open fun onDetach(view: V) {}
}
