package com.zhuinden.simplestackdemoexamplefragments.util

import com.zhuinden.simplestack.Bundleable

/**
 * Created by Owner on 2017. 01. 27..
 */

abstract class BasePresenter<F : BaseFragment<F, P>, P : BasePresenter<F, P>> : Bundleable {
    var fragment: F? = null

    fun attachFragment(fragment: F) {
        this.fragment = fragment
        onAttach(fragment)
    }

    fun detachFragment(fragment: F) {
        onDetach(fragment)
        this.fragment = null
    }

    protected abstract fun onAttach(fragment: F)

    protected abstract fun onDetach(fragment: F)
}
