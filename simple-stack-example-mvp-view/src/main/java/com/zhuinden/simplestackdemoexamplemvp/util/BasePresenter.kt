package com.zhuinden.simplestackdemoexamplemvp.util

import android.view.View

/**
 * Created by Owner on 2017. 01. 27..
 */

abstract class BasePresenter<V : View> {
    var view: V? = null
        private set

    fun attachView(view: V) {
        this.view = view
        onAttach(view)
    }

    fun detachView(view: V) {
        onDetach(view)
        this.view = null
    }

    protected abstract fun onAttach(view: V)

    protected abstract fun onDetach(view: V)
}
