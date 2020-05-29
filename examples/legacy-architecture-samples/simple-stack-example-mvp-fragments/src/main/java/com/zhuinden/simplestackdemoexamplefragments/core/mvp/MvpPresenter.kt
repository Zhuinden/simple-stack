package com.zhuinden.simplestackdemoexamplefragments.core.mvp

interface MvpPresenter<V> {
    fun attachView(view: V)

    fun detachView(view: V)
}