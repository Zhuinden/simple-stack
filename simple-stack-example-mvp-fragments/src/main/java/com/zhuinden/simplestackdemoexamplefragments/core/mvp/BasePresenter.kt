package com.zhuinden.simplestackdemoexamplefragments.core.mvp

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

abstract class BasePresenter<V> : MvpPresenter<V> {
    var view: V? = null

    override final fun attachView(view: V) {
        this.view = view
        onAttach(view)
    }

    override final fun detachView(view: V) {
        onDetach(view)
        this.view = null
    }

    protected open fun onAttach(view: V) {}

    protected open fun onDetach(view: V) {}
}
