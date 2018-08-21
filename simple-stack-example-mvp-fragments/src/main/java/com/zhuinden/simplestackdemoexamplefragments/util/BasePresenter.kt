package com.zhuinden.simplestackdemoexamplefragments.util

import com.zhuinden.simplestack.Bundleable

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

abstract class BasePresenter<V : BaseViewContract> : Bundleable {
    var view: V? = null

    fun attachFragment(fragment: V) {
        this.view = fragment
        onAttach(fragment)
    }

    fun detachFragment(fragment: V) {
        onDetach(fragment)
        this.view = null
    }

    protected abstract fun onAttach(view: V)

    protected abstract fun onDetach(view: V)
}
